package com.walla_ho.TabelaFipe.principal;

import com.walla_ho.TabelaFipe.model.Dados;
import com.walla_ho.TabelaFipe.model.Modelos;
import com.walla_ho.TabelaFipe.model.Veiculo;
import com.walla_ho.TabelaFipe.service.ConsumoAPI;
import com.walla_ho.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class main {

    private Scanner scan = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private String endereco;
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados converter = new ConverteDados();

    public void exibeMenu(){

        //===========================================
        // FILTRAR TIPO VEICULO
        var menu = """
                *** VEICULO ***
                Carro
                Moto
                Caminhão
                
                Digite umas das opções para consulta:
                """;

        System.out.println(menu);
        String escolha = scan.nextLine();
        escolha = escolha.toUpperCase();

        if (escolha.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        } else if (escolha.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        //===========================================
        // MOSTRAR MARCA
        var json = consumo.obterDados(endereco);
        var marcas = converter.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        //===========================================
        // FILTRAR MARCA
        System.out.println("Informe o código da marca para consulta:");
        escolha = scan.nextLine();

        endereco = endereco + "/" + escolha + "/modelos";

        json = consumo.obterDados(endereco);

        //===========================================
        // MOSTRAR MODELO
        var modeloLista = converter.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        //===========================================
        // FILTRAR MODELO
        System.out.println("Informe o nome do modelo para consulta:");
        var nomeVeiculo = scan.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("Modelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        //===========================================
        // FILTRAR O VEICULO
        System.out.println("Digite o codigo do veiculo:");
        escolha = scan.nextLine();

        endereco = endereco + "/" + escolha + "/anos";

        json = consumo.obterDados(endereco);
        List<Dados> anos = converter.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = converter.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        //===========================================
        // FILTRAR O VEICULO
        System.out.println("\nTodos veiculos filtrados por ano:");
        veiculos.forEach(System.out::println);
    }
}
