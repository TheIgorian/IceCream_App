from typing import Union
from psycopg_pool import ConnectionPool
from psycopg import Connection
from datetime import datetime
import json

class DatabaseManager:
    __slots__ = ('__connection_pool',)

    def __init__(self, min_conn_num: int, max_conn_num: int, host: str, port: int, user: str, password: str,
                 database: str, pool_name: str):
        self.__connection_pool = ConnectionPool(
            conninfo=f"host={host} port={port} dbname={database} user={user} password={password}",
            connection_class=Connection,
            min_size=min_conn_num,
            max_size=max_conn_num,
            open=True,
            name=pool_name,
            timeout=180.0,
        )

    @property
    def connection_pool(self):
        return self.__connection_pool

    def close_all_connections(self):
        self.__connection_pool.closeall()

    async def fetch_data_from_db(self, query: str, parameters: Union[list, dict] = None, fetchone: bool = False):
        with self.__connection_pool.connection() as free_connection:
            with free_connection as connection, connection.cursor() as cursor:
                cursor.execute(query, parameters)
                if fetchone:
                    data = cursor.fetchone()
                else:
                    data = cursor.fetchall()
                return data

    async def modify_data_in_db(self, query: str, parameters: Union[list, dict]):
        with self.__connection_pool.connection() as free_connection:
            with free_connection as connection, connection.cursor() as cursor:
                cursor.execute(query, parameters)
                return cursor.rowcount

    async def call_process_order(self, param_dict: dict):
        query = "CALL public.process_order(%s, 0)"
        parameters = (json.dumps(param_dict),)
        data = await self.fetch_data_from_db(query, parameters, True)
        return data[0]

    async def get_flavor_for_company(self, param_dict: dict):
        query = """
            SELECT i.Name, CAST(cf.mass AS FLOAT) AS mass, cf.price, i.photo
            FROM Ice_Cream_Flavor i
            JOIN Company_Flavor cf ON i.Flavor_ID = cf.Flavor_ID
            WHERE cf.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_additive_for_company(self, param_dict: dict):
        query = """
            SELECT a.Name, CAST(ca.mass AS FLOAT) AS mass, ca.price, a.photo
            FROM Additive a
            JOIN Company_Additive ca ON a.Additive_ID = ca.Additive_ID
            WHERE ca.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_cone_for_company(self, param_dict: dict):
        query = """
            SELECT c.type, cp.stock_quantity, c.photo
            FROM Cone c
            JOIN Cone_Point cp ON c.Cone_ID = cp.Cone_ID
            WHERE cp.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_all_object_for_company(self, param_dict: dict):
        data = {
            "cone": await self.get_cone_for_company(param_dict),
            "additive": await self.get_additive_for_company(param_dict),
            "flavor": await self.get_flavor_for_company(param_dict)
        }
        return data

    async def change_ice_cream(self, param_dict: dict):
        set_clause = []
        parameters = []

        if "mass" in param_dict:
            if param_dict.get("operand") in ["+", "-"]:
                set_clause.append(f"mass = mass {param_dict.get('operand')} %s")
            else:
                set_clause.append("mass = %s")
            parameters.append(param_dict.get("mass"))

        if "price" in param_dict:
            set_clause.append("price = %s")
            parameters.append(param_dict.get("price"))

        if not set_clause:
            return "No mass or price provided to update."

        if param_dict.get("flavor_name"):
            flavor_names = param_dict.get("flavor_name")
            if isinstance(flavor_names, list):  # Перевірка чи переданий список смаків
                query = f"""
                            UPDATE Company_Flavor
                            SET {', '.join(set_clause)}
                            WHERE Flavor_ID IN (SELECT Flavor_ID FROM Ice_Cream_Flavor WHERE Name = ANY(%s))
                            AND Point_ID = %s;
                        """
            else:  # Якщо передано один смак
                query = f"""
                            UPDATE Company_Flavor
                            SET {', '.join(set_clause)}
                            WHERE Flavor_ID = (SELECT Flavor_ID FROM Ice_Cream_Flavor WHERE Name = %s)
                            AND Point_ID = %s;
                        """
            parameters.extend([flavor_names, param_dict.get("point")])
        else:
            query = f"""
                UPDATE Company_Flavor
                SET {', '.join(set_clause)}
                WHERE Point_ID = %s;
            """
            parameters.append(param_dict.get("point"))
        data = await self.modify_data_in_db(query, parameters)
        return data

    async def change_additive(self, param_dict: dict):
        set_clause = []
        parameters = []

        if "mass" in param_dict:
            if param_dict.get("operand") in ["+", "-"]:
                set_clause.append(f"mass = mass {param_dict.get('operand')} %s")
            else:
                set_clause.append("mass = %s")
            parameters.append(param_dict.get("mass"))

        if "price" in param_dict:
            set_clause.append("price = %s")
            parameters.append(param_dict.get("price"))

        if not set_clause:
            return "No mass or price provided to update."

        if param_dict.get("additive_name"):
            additive_names = param_dict.get("additive_name")
            if isinstance(additive_names, list):  # Перевірка чи переданий список смаків
                query = f"""
                    UPDATE Company_Additive
                    SET {', '.join(set_clause)}
                    WHERE Additive_ID IN (SELECT Additive_ID FROM Additive WHERE Name = ANY(%s))
                    AND Point_ID = %s;
                """
            else:
                query = f"""
                    UPDATE Company_Additive
                    SET {', '.join(set_clause)}
                    WHERE Additive_ID = (SELECT Additive_ID FROM Additive WHERE Name = %s)
                    AND Point_ID = %s;
                """
            parameters.extend([additive_names, param_dict.get("point")])
        else:
            query = f"""
                UPDATE Company_Additive
                SET {', '.join(set_clause)}
                WHERE Point_ID = %s;
            """
            parameters.append(param_dict.get("point"))

        data = await self.modify_data_in_db(query, parameters)
        return data

    async def change_cone(self, param_dict: dict):
        if param_dict.get("operand") in ["+", "-"]:
            set_stock_quantity = f"stock_quantity = stock_quantity {param_dict.get('operand')} {param_dict.get('number')}"
        else:
            set_stock_quantity = f"stock_quantity = {param_dict.get('number')}"

        if param_dict.get("cone_name"):
            cone_names = param_dict.get("cone_name")
            if isinstance(cone_names, list):  # Перевірка чи переданий список смаків
                query = f"""
                    UPDATE cone_point
                    SET {set_stock_quantity}
                    WHERE Cone_ID IN (SELECT Cone_ID FROM Cone WHERE Type = ANY(%s))
                    AND Point_ID = %s;
                """
            else:
                query = f"""
                    UPDATE cone_point
                    SET {set_stock_quantity}
                    WHERE Cone_ID = (SELECT Cone_ID FROM Cone WHERE Type = %s)
                    AND Point_ID = %s;
                """
            parameters = [cone_names, param_dict.get("point")]
        else:
            query = f"""
                UPDATE cone_point
                SET {set_stock_quantity}
                WHERE Point_ID = %s;
            """
            parameters = [param_dict.get("point"),]

        data = await self.modify_data_in_db(query, parameters)
        return data

    async def add_ice_cream(self, param_dict: dict):
        query = """
            WITH inserted AS (
                INSERT INTO company_flavor (Point_ID, Flavor_ID, Price, Mass)
                SELECT %s, Flavor_ID, %s, %s
                FROM ice_cream_flavor
                WHERE Name = %s
                  AND NOT EXISTS (
                      SELECT 1 
                      FROM company_flavor cf
                      JOIN ice_cream_flavor icf ON cf.Flavor_ID = icf.Flavor_ID
                      WHERE cf.Point_ID = %s AND icf.Name = %s
                  )
                RETURNING Flavor_ID
            )
            SELECT icf.Photo
            FROM ice_cream_flavor icf
            INNER JOIN inserted i ON icf.Flavor_ID = i.Flavor_ID;
        """
        parameters = (
            param_dict.get("point"),
            param_dict.get("price"),
            param_dict.get("mass"),
            param_dict.get("flavor_name"),
            param_dict.get("point"),
            param_dict.get("flavor_name"),
        )
        result = await self.fetch_data_from_db(query, parameters)
        if result:
            return result[0][0]  # Повертає фото смаку морозива
        else:
            change_ice_cream
            return None  # Якщо не вдалося додати новий смак морозива або знайти фото

    async def add_additive(self, param_dict: dict):
        query = """
            WITH inserted AS (
                INSERT INTO company_additive (Point_ID, Additive_ID, Price, Mass)
                SELECT %s, Additive_ID, %s, %s
                FROM Additive
                WHERE Name = %s
                  AND NOT EXISTS (
                      SELECT 1 
                      FROM company_additive ca
                      JOIN Additive a ON ca.Additive_ID = a.Additive_ID
                      WHERE ca.Point_ID = %s AND a.Name = %s
                  )
                RETURNING Additive_ID
            )
            SELECT a.Photo
            FROM Additive a
            INNER JOIN inserted i ON a.Additive_ID = i.Additive_ID;
        """
        parameters = (
            param_dict.get("point"),
            param_dict.get("price"),
            param_dict.get("mass"),
            param_dict.get("additive_name"),
            param_dict.get("point"),
            param_dict.get("additive_name"),
        )
        result = await self.fetch_data_from_db(query, parameters)
        if result:
            return result[0][0]  # Повертає фото добавки
        else:
            await self.change_additive(param_dict)
            return None  # Якщо не вдалося додати нову добавку або знайти фото

    async def add_cone(self, param_dict: dict):
        query = """
            WITH inserted AS (
                INSERT INTO cone_point (Point_ID, Cone_ID, Stock_Quantity)
                SELECT %s, Cone_ID, %s
                FROM Cone
                WHERE Type = %s
                  AND NOT EXISTS (
                      SELECT 1
                      FROM cone_point cp
                      WHERE cp.Point_ID = %s AND cp.Cone_ID = (
                          SELECT Cone_ID
                          FROM Cone
                          WHERE Type = %s
                      )
                  )
                RETURNING Cone_ID
            )
            SELECT c.Photo
            FROM Cone c
            INNER JOIN inserted i ON c.Cone_ID = i.Cone_ID;
        """
        parameters = (
            param_dict.get("point"),
            param_dict.get("number"),
            param_dict.get("cone_name"),
            param_dict.get("point"),
            param_dict.get("cone_name")
        )
        result = await self.fetch_data_from_db(query, parameters)
        if result:
            return result[0][0]  # Повертає фото нового ріжка
        else:
            await self.change_cone(param_dict)
            return None  # Якщо не вдалося додати новий ріжок або знайти фото

    async def get_order(self, param_dict: dict):
        query = """
            SELECT 
                c.Type AS cone_type,
                STRING_AGG(DISTINCT i.Name, ', ') AS flavors,
                STRING_AGG(DISTINCT a.Name, ', ') AS toppings,
                op.Quantity AS quantity,
                CAST(p.Price AS FLOAT) AS price_per_unit,
                CAST(p.Price * op.Quantity AS FLOAT) AS total_price
            FROM Orders o
            JOIN Order_Product op ON o.Order_ID = op.Order_ID
            JOIN Product p ON op.Product_ID = p.Product_ID
            JOIN Cone_Point cp ON p.Cone_Point_ID = cp.Cone_Point_ID
            JOIN Cone c ON cp.Cone_ID = c.Cone_ID
            JOIN Product_Flavor pf ON p.Product_ID = pf.Product_ID
            JOIN Company_Flavor cf ON pf.Company_Flavor_ID = cf.Company_Flavor_ID
            JOIN Ice_Cream_Flavor i ON cf.Flavor_ID = i.Flavor_ID
            JOIN Product_Additive pad ON p.Product_ID = pad.Product_ID
            JOIN Company_Additive ca ON pad.Company_Additive_ID = ca.Company_Additive_ID
            JOIN Additive a ON ca.Additive_ID = a.Additive_ID
            JOIN Company_Point cpnt ON cpnt.Point_ID = cp.Point_ID
            WHERE  cpnt.Point_ID = %s and o.Order_Date = %s
            GROUP BY p.Product_ID, c.Type, op.Quantity, p.Price
        """
        parameters = (param_dict.get("point"), param_dict.get("date"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_employee_for_company(self, param_dict: dict):
        query = """
            SELECT first_name, last_name, position, login, phone
            FROM Employee
            WHERE Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def delete_employee(self, param_dict: dict):
        query = """
            DELETE FROM Employee
            WHERE login = %s;
        """
        parameters = (param_dict.get("login"),)
        result = await self.modify_data_in_db(query, parameters)
        if result > 0:
            return True
        else:
            return False

    async def delete_flavor(self, param_dict: dict):
        query = """
            DELETE FROM Company_Flavor
            USING Ice_Cream_Flavor
            WHERE Company_Flavor.Flavor_ID = Ice_Cream_Flavor.Flavor_ID
              AND Ice_Cream_Flavor.Name = %s
              AND Company_Flavor.Point_ID = %s;
        """
        parameters = (param_dict.get("flavor_name"), param_dict.get("point_id"))
        result = await self.modify_data_in_db(query, parameters)
        if result > 0:
            return True
        else:
            return False

    async def delete_additive(self, param_dict: dict):
        query = """
            DELETE FROM Company_Additive
            USING Additive
            WHERE Company_Additive.Additive_ID = Additive.Additive_ID
              AND Additive.Name = %s
              AND Company_Additive.Point_ID = %s;
        """
        parameters = (param_dict.get("additive_name"), param_dict.get("point_id"))
        result = await self.modify_data_in_db(query, parameters)
        if result > 0:
            return True
        else:
            return False

    async def delete_cone(self, param_dict: dict):
        query = """
            DELETE FROM Cone_Point
            USING Cone
            WHERE Cone_Point.Cone_ID = Cone.Cone_ID
              AND Cone.Type = %s
              AND Cone_Point.Point_ID = %s;
        """
        parameters = (param_dict.get("cone_type"), param_dict.get("point_id"))
        result = await self.modify_data_in_db(query, parameters)
        if result > 0:
            return True
        else:
            return False

    async def add_employee(self, param_dict: dict):

        query = """
            INSERT INTO Employee (first_name, last_name, position, login, phone, password, Point_ID)
            SELECT %s, %s, %s, %s, %s, %s, %s
            WHERE NOT EXISTS (
                SELECT 1 FROM Employee WHERE login = %s
            );
        """
        parameters = (
            param_dict.get("first_name"),
            param_dict.get("last_name"),
            param_dict.get("position"),
            param_dict.get("login"),
            param_dict.get("phone"),
            param_dict.get("password"),
            param_dict.get("point_id"),
            param_dict.get("login"),
        )

        result = await self.modify_data_in_db(query, parameters)
        if result == 1:
            return True
        else:
            return False

    async def password_verification(self, param_dict: dict):
        query = """
            SELECT Point_ID::text, employee_id::text, position
            FROM Employee
            WHERE login = %s AND Password = %s;
        """
        parameters = (param_dict.get("employee"), param_dict.get("password"))
        data = await self.fetch_data_from_db(query, parameters, True)

        if data:
            return data
        else:
            return None

    async def get_all_company(self, param_dict: dict):
        query = """
            SELECT * 
            FROM company_point;
        """
        data = await self.fetch_data_from_db(query)
        return data

    async def get_stat_flavor(self, param_dict: dict):
        query = """
            SELECT 
                f.Name AS flavor_name, -- Назва смаку
                CAST(SUM(pf.Mass * op.Quantity) AS FLOAT) AS total_mass -- Загальна маса (маса * кількість)
            FROM Orders o
            JOIN Order_Product op ON o.Order_ID = op.Order_ID
            JOIN Product p ON op.Product_ID = p.Product_ID
            JOIN Product_Flavor pf ON p.Product_ID = pf.Product_ID
            JOIN Company_Flavor cf ON pf.Company_Flavor_ID = cf.Company_Flavor_ID
            JOIN Ice_Cream_Flavor f ON cf.Flavor_ID = f.Flavor_ID
            WHERE cf.Point_ID = %s
            GROUP BY f.Name
            ORDER BY total_mass DESC;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)

        return data

    async def get_stat_additive(self, param_dict: dict):
        query = """
            SELECT 
                a.Name AS additive_name, -- Назва добавки
                CAST(SUM(pa.Mass * op.Quantity) AS FLOAT) AS total_mass -- Загальна маса (маса * кількість)
            FROM Orders o
            JOIN Order_Product op ON o.Order_ID = op.Order_ID
            JOIN Product p ON op.Product_ID = p.Product_ID
            JOIN Product_Additive pa ON p.Product_ID = pa.Product_ID
            JOIN Company_Additive ca ON pa.Company_Additive_ID = ca.Company_Additive_ID
            JOIN Additive a ON ca.Additive_ID = a.Additive_ID
            WHERE ca.Point_ID = %s
            GROUP BY a.Name
            ORDER BY total_mass DESC;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)

        return data

    async def get_stat_cone(self, param_dict: dict):
        query = """
            SELECT 
                c.Type AS cone_type, -- Тип ріжка
                SUM(op.Quantity) AS total_quantity -- Загальна кількість
            FROM Orders o
            JOIN Order_Product op ON o.Order_ID = op.Order_ID
            JOIN Product p ON op.Product_ID = p.Product_ID
            JOIN Cone_Point cp ON p.Cone_Point_ID = cp.Cone_Point_ID
            JOIN Cone c ON cp.Cone_ID = c.Cone_ID
            WHERE cp.Point_ID = %s
            GROUP BY c.Type
            ORDER BY total_quantity DESC;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)

        return data

    async def get_stat_all(self, param_dict: dict):
        data = {
            "cone": await self.get_stat_cone(param_dict),
            "additive": await self.get_stat_additive(param_dict),
            "flavor": await self.get_stat_flavor(param_dict)
        }
        return data

    async def get_flavors_not_point(self, param_dict: str):
        query = """
            SELECT i.Name
            FROM Ice_Cream_Flavor i
            WHERE i.Flavor_ID NOT IN (
                SELECT cf.Flavor_ID
                FROM Company_Flavor cf
                WHERE cf.Point_ID = %s
            );
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_additives_not_point(self, param_dict: dict):
        query = """
            SELECT a.Name
            FROM Additive a
            WHERE a.Additive_ID NOT IN (
                SELECT ca.Additive_ID
                FROM Company_Additive ca
                WHERE ca.Point_ID = %s
            );
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_cones_not_point(self, param_dict: dict):
        query = """
            SELECT c.Type
            FROM Cone c
            WHERE c.Cone_ID NOT IN (
                SELECT cp.Cone_ID
                FROM Cone_Point cp
                WHERE cp.Point_ID = %s
            );
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data



