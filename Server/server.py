from sanic import Sanic, response
import yaml
from database_manager import DatabaseManager
import asyncio


with open('param.yml', 'r') as stream:
    try:
        db_dict = yaml.safe_load(stream)["database"]
        db_manager = DatabaseManager(db_dict["db_min_con"], db_dict["db_max_con"], db_dict["host"],
                                     db_dict["db_port"], db_dict["db_user"], db_dict["db_password"],
                                     db_dict["db_name"], db_dict["pool_name"])
    except yaml.YAMLError as exc:
        print(exc)


async def create_run():
    extension_query = "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"

    create_tables_query = """
    CREATE TABLE IF NOT EXISTS Company_Point (
        Point_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Address VARCHAR,
        Phone VARCHAR
    );

    CREATE TABLE IF NOT EXISTS Employee (
        Employee_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Point_ID UUID REFERENCES Company_Point(Point_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        First_Name VARCHAR,
        Last_Name VARCHAR,
        Position VARCHAR,
        Password VARCHAR
    );

    CREATE TABLE IF NOT EXISTS Ice_Cream_Flavor (
        Flavor_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Name VARCHAR
    );

    CREATE TABLE IF NOT EXISTS Additive (
        Additive_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Name VARCHAR
    );

    CREATE TABLE IF NOT EXISTS Cone (
        Cone_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Type VARCHAR
    );

    CREATE TABLE IF NOT EXISTS Orders (
        Order_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Order_Date DATE,
        Employee_ID UUID REFERENCES Employee(Employee_ID) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Company_Additive (
        Company_Additive_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Point_ID UUID REFERENCES Company_Point(Point_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Additive_ID UUID REFERENCES Additive(Additive_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Price INT,
        Mass FLOAT NULL
    );

    CREATE TABLE IF NOT EXISTS Company_Flavor (
        Company_Flavor_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Point_ID UUID REFERENCES Company_Point(Point_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Flavor_ID UUID REFERENCES Ice_Cream_Flavor(Flavor_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Price INT,
        Mass FLOAT NULL
    );

    CREATE TABLE IF NOT EXISTS Cone_Point (
        Cone_Point_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Point_ID UUID REFERENCES Company_Point(Point_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Cone_ID UUID REFERENCES Cone(Cone_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Stock_Quantity INT NULL
    );

    CREATE TABLE IF NOT EXISTS Product (
        Product_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Name VARCHAR,
        Price DECIMAL,
        Cone_Point_ID UUID REFERENCES Cone_Point(Cone_Point_ID) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Order_Product (
        Order_Product_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Order_ID UUID REFERENCES Orders(Order_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Product_ID UUID REFERENCES Product(Product_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Quantity INT
    );

    CREATE TABLE IF NOT EXISTS Product_Flavor (
        Product_Flavor_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Product_ID UUID REFERENCES Product(Product_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Company_Flavor_ID UUID REFERENCES Company_Flavor(Company_Flavor_ID) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Product_Additive (
        Product_Additive_ID UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        Product_ID UUID REFERENCES Product(Product_ID) ON DELETE CASCADE ON UPDATE CASCADE,
        Company_Additive_ID UUID REFERENCES Company_Additive(Company_Additive_ID) ON DELETE CASCADE ON UPDATE CASCADE
    );
    """

    await db_manager.modify_data_in_db(extension_query, None)
    await db_manager.modify_data_in_db(create_tables_query, None)

app = Sanic("MySanicApp")

@app.route('/', methods=['POST'])
async def hello(request):
    print("hello")
    return response.json({"result": "OK"})

@app.route('/database', methods=['POST'])
async def db_function(request):
    try:
        data = request.json
        function_to_call = getattr(db_manager, data.get('function_name'), None)
        if function_to_call:
            if callable(function_to_call):
                result = await function_to_call(data.get('param_dict'))
                print(result)
                return response.json({"result": result})
            else:
                print(f"Error in request: Function {data.get('function_name')} does not exist or cannot be called")
                return response.json({"error": f"Error in request: Function {data.get('function_name')} does not exist or cannot be called"})
        else:
            print("Error in request: no function_name or this function was not found")
            return response.json({"error": "Error in request: No function_name or this function was not found"})
    except Exception as e:
        print(f"An error occurred:\n{e}")
        return response.json({"error": f"An internal error occurred:\n{e}"}, status=500)

if __name__ == '__main__':
    asyncio.run(create_run())
    app.run(host='0.0.0.0', port=5000)