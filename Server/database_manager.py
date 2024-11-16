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
        return data

    async def get_flavor_for_company(self, param_dict: dict):
        query = """
            SELECT i.Name, cf.mass, cf.price
            FROM Ice_Cream_Flavor i
            JOIN Company_Flavor cf ON i.Flavor_ID = cf.Flavor_ID
            WHERE cf.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_additive_for_company(self, param_dict: dict):
        query = """
            SELECT a.Name, ca.mass, ca.price
            FROM Additive a
            JOIN Company_Additive ca ON a.Additive_ID = ca.Additive_ID
            WHERE ca.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def get_cone_for_company(self, param_dict: dict):
        query = """
            SELECT c.type, cp.stock_quantity
            FROM Cone c
            JOIN Cone_Point cp ON c.Cone_ID = cp.Cone_ID
            WHERE cp.Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def сhange_ice_cream(self, param_dict: dict):
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

    async def сhange_additive(self, param_dict: dict):
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
            INSERT INTO Company_Flavor (Point_ID, Flavor_ID, Price, Mass)
            SELECT %s, Flavor_ID, %s, %s
            FROM ice_cream_flavor
            WHERE Name = ANY(%s);
        """
        parameters = (param_dict.get("point"), param_dict.get("price"), param_dict.get("mass"), param_dict.get("flavor_name"),)
        data = await self.modify_data_in_db(query, parameters)
        return data

    async def add_additive(self, param_dict: dict):
        query = """
            INSERT INTO company_additive (Point_ID, additive_id, Price, Mass)
            SELECT %s, additive_id, %s, %s
            FROM additive
            WHERE Name = ANY(%s);
        """
        parameters = (param_dict.get("point"), param_dict.get("price"), param_dict.get("mass"), param_dict.get("additive_name"),)
        data = await self.modify_data_in_db(query, parameters)
        return data

    async def add_cone(self, param_dict: dict):
        query = """
            INSERT INTO cone_point (Point_ID, cone_id, stock_quantity)
            SELECT %s, cone_id, %s
            FROM cone
            WHERE type = ANY(%s);
        """
        parameters = (param_dict.get("point"), param_dict.get("number"), param_dict.get("cone_name"),)
        data = await self.modify_data_in_db(query, parameters)
        return data

    async def get_employee_for_company(self, param_dict: dict):
        query = """
            SELECT employee_id, first_name, last_name, position
            FROM Employee
            WHERE Point_ID = %s;
        """
        parameters = (param_dict.get("point"),)
        data = await self.fetch_data_from_db(query, parameters)
        return data

    async def password_verification(self, param_dict: dict):
        query = """
            SELECT employee_id
            FROM Employee
            WHERE Employee_ID = %s AND Password = %s;
        """
        parameters = (param_dict.get("employee"), param_dict.get("password"))
        data = await self.fetch_data_from_db(query, parameters, True)

        if data:
            return data
        else:
            return "Invalid Employee or Password"

    async def get_all_company(self, param_dict: dict):
        query = """
            SELECT * 
            FROM company_point;
        """
        data = await self.fetch_data_from_db(query)
        return data

