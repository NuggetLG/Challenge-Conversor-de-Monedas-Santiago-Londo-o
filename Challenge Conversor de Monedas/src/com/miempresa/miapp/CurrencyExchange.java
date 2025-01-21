package com.miempresa.miapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class CurrencyExchange {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Obtener las tasas de cambio desde la API
        JsonObject rates = fetchExchangeRates();

        if (rates == null) {
            System.out.println("No se pudieron obtener las tasas de cambio. Verifique su conexión a Internet.");
            return;
        }

        System.out.println("Bienvenido al conversor de monedas");
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Convertir de ARS a otra moneda");
            System.out.println("2. Convertir de BOB a otra moneda");
            System.out.println("3. Convertir de BRL a otra moneda");
            System.out.println("4. Convertir de COP a otra moneda");
            System.out.println("5. Salir");
            System.out.print("Ingrese su opción: ");

            int opcion = scanner.nextInt();

            if (opcion == 5) {
                continuar = false;
                System.out.println("Gracias por usar el conversor de monedas. ¡Hasta pronto!");
            } else {
                System.out.print("Ingrese la cantidad a convertir: ");
                double cantidad = scanner.nextDouble();

                System.out.print("Ingrese el código de la moneda destino (USD, ARS, BOB, BRL, etc.): ");
                String monedaDestino = scanner.next().toUpperCase();

                if (!rates.has(monedaDestino)) {
                    System.out.println("Moneda destino no válida.");
                    continue;
                }

                double tasaOrigen;
                switch (opcion) {
                    case 1 -> tasaOrigen = rates.get("ARS").getAsDouble();
                    case 2 -> tasaOrigen = rates.get("BOB").getAsDouble();
                    case 3 -> tasaOrigen = rates.get("BRL").getAsDouble();
                    case 4 -> tasaOrigen = rates.get("COP").getAsDouble();
                    default -> {
                        System.out.println("Opción no válida.");
                        continue;
                    }
                }

                double tasaDestino = rates.get(monedaDestino).getAsDouble();
                double resultado = convertir(cantidad, tasaOrigen, tasaDestino);

                System.out.printf("Resultado: %.2f en %s%n", resultado, monedaDestino);
            }
        }
    }

    /**
     * Obtiene las tasas de cambio desde una API externa.
     *
     * @return Objeto JsonObject con las tasas de cambio, o null si ocurre un error.
     */
    private static JsonObject fetchExchangeRates() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.exchangerate-api.com/v4/latest/USD"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            return jsonObject.getAsJsonObject("rates");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convierte una cantidad de una moneda a otra usando las tasas de cambio.
     *
     * @param cantidad    Cantidad a convertir.
     * @param tasaOrigen  Tasa de la moneda origen.
     * @param tasaDestino Tasa de la moneda destino.
     * @return Cantidad convertida.
     */
    private static double convertir(double cantidad, double tasaOrigen, double tasaDestino) {
        return (cantidad / tasaOrigen) * tasaDestino;
    }
}