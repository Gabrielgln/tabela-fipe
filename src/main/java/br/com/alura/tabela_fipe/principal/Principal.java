package br.com.alura.tabela_fipe.principal;

import br.com.alura.tabela_fipe.model.Dados;
import br.com.alura.tabela_fipe.model.Modelos;
import br.com.alura.tabela_fipe.model.Veiculo;
import br.com.alura.tabela_fipe.service.ConsumoApi;
import br.com.alura.tabela_fipe.service.ConverteDados;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private static final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados(new ObjectMapper());

    public void exibeMenu(){
        String menu = """
                *** OPÇÕES ***
                
                1 - Carro
                2 - Moto
                3 - Caminhão
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        int opcao = leitura.nextInt();
        String endereco;

        switch (opcao){
            case 1:
               endereco = URL_BASE + "carros/marcas";
               break;
            case 2:
                endereco = URL_BASE + "motos/marcas";
                break;
            case 3:
                endereco = URL_BASE + "caminhoes/marcas";
                break;
            default:
                System.out.println("Opção inválida!");
                return;
        }


        String json = consumoApi.obterDados(endereco);
        System.out.println(json);

        List<Dados> marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        leitura.nextLine();
        String codigoMarca = leitura.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        Modelos modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos desse marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        String nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .toList();

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite por favor o código do modelo para buscar os valores de avaliação: ");
        String codigoModelo = leitura.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (Dados ano : anos) {
            var enderecoAnos = endereco + "/" + ano.codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por anos: ");
        veiculos.forEach(System.out::println);
    }
}
