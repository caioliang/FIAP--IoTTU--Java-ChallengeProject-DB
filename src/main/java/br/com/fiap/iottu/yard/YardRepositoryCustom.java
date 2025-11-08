package br.com.fiap.iottu.yard;

public interface YardRepositoryCustom {

    Integer calcularVagasDisponiveis(Long idYard);

    String gerarJsonYard(Long idYard);

    void gerarRelatorioMotosPorYard();
}
