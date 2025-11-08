package br.com.fiap.iottu.yard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.io.Reader;
import java.sql.Clob;

@Repository
@Transactional
public class YardRepositoryImpl implements YardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer calcularVagasDisponiveis(Long idYard) {
        var query = entityManager.createNativeQuery(
                "SELECT PKG_RELATORIOS_PATIO.FN_CALCULAR_VAGAS_DISPONIVEIS(:idYard) FROM dual"
        );
        query.setParameter("idYard", idYard);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).intValue() : null;
    }

    @Override
    public String gerarJsonYard(Long idYard) {
        var query = entityManager.createNativeQuery(
                "SELECT PKG_RELATORIOS_PATIO.FNC_GERAR_JSON_PATIO(:idYard) FROM dual"
        );
        query.setParameter("idYard", idYard);

        Object result = query.getSingleResult();

        if (result == null) return "{}";

        if (result instanceof java.sql.Clob clob) {
            try (Reader reader = clob.getCharacterStream()) {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, bytesRead);
                }
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException("Erro ao converter CLOB para String", e);
            }
        }

        return result.toString();
    }

    @Override
    public void gerarRelatorioMotosPorYard() {
        var query = entityManager.createStoredProcedureQuery("PKG_RELATORIOS_PATIO.PRC_RELATORIO_MOTOS_POR_PATIO");
        query.execute();
    }
}
