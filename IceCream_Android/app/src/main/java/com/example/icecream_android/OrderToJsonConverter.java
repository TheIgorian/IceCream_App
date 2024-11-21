package com.example.icecream_android;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class OrderToJsonConverter {

    public static JSONObject convertOrdersToJson(String employeeId, String pointId, List<IceCreamOrder> orders) {
        JSONObject result = new JSONObject();

        // Основной объект запроса
        try {
            result.put("function_name", "call_process_order");

            // Параметры запроса
            JSONObject paramDict = new JSONObject();
            paramDict.put("employee", employeeId);
            paramDict.put("point", pointId);

            // Массив заказов
            JSONArray ordersArray = new JSONArray();

            for (IceCreamOrder order : orders) {
                JSONObject orderJson = new JSONObject();

                // Добавляем информацию о вкусах
                JSONObject flavorsJson = new JSONObject();
                for (int i = 0; i < order.getFlavors().size(); i++) {
                    String flavorName = order.getFlavors().get(i);
                    double quantity = order.getFlavors_prices().get(i) * order.getCount_flavors();  // Количество умножаем на count_flavors
                    flavorsJson.put(flavorName, String.format("%.2f", quantity));  // Форматируем до 2 знаков после запятой
                }
                orderJson.put("flavor", flavorsJson);

                // Добавляем информацию о топпингах
                JSONObject toppingsJson = new JSONObject();
                for (int i = 0; i < order.getToppings().size(); i++) {
                    String toppingName = order.getToppings().get(i);
                    double quantity = order.getToppings_prices().get(i) * order.getCount_toppings();  // Количество умножаем на count_toppings
                    toppingsJson.put(toppingName, String.format("%.2f", quantity));  // Форматируем до 2 знаков после запятой
                }
                orderJson.put("additive", toppingsJson);

                // Добавляем информацию о ріжку
                orderJson.put("cone", order.getHorn());

                // Добавляем количество порций
                orderJson.put("number", order.getCount_flavors());

                // Добавляем заказ в массив
                ordersArray.put(orderJson);
            }

            // Вставляем массив заказов в параметр
            paramDict.put("orders", ordersArray);

            // Вставляем все параметры в результат
            result.put("param_dict", paramDict);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
