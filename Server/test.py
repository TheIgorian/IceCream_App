import requests

url = "http://127.0.0.1:5000/database"
headers = {"Content-Type": "application/json"}
# data = {
#     #"function_name": "call_process_order",
# 	"function_name": "get_cone_for_company",
#     "param_dict": {
# 		"employee": "20d7cdd1-72c3-4a16-b192-6213b75271ec",
# 		"point": "f7c654aa-60de-40c1-ac03-466a31882698",
# 		"orders": [
# 			{"flavor": {"Шоколад": "0.1", "Полуниця": "0.3"}, "additive": {"Горішки": 0.05}, "cone": "Вафельний ріжок", "number": 3},
#      		{"flavor": {"Ваніль": "0.2", "Полуниця": "0.3"}, "additive": {"Ізюм": 0.05, "Шоколадний топпінг": "0.03"}, "cone": "Солоний ріжок", "number": 1}
# 		]
# 	}
# }

# data = {
#     "function_name": "change_cone",
#     "param_dict": {
#         "point": "f7c654aa-60de-40c1-ac03-466a31882698",  # Ідентифікатор точки продажу
#         "operand": "+",
#         "cone_name": ["Солодкий ріжок", "Картонний"],
#         "number": 10,
#     }
# }

data = {
    "function_name": "add_cone",
    "param_dict": {
        "point": "f7c654aa-60de-40c1-ac03-466a31882698",  # Ідентифікатор точки продажу
        "cone_name": ["Солодкий ріжок", "Картонний"],
        "number": 10,
    }
}

response = requests.post(url, json=data, headers=headers)
print(response.json())