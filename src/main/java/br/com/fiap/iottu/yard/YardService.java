package br.com.fiap.iottu.yard;

import br.com.fiap.iottu.antenna.Antenna;
import br.com.fiap.iottu.antenna.AntennaService;
import br.com.fiap.iottu.motorcycle.Motorcycle;
import br.com.fiap.iottu.motorcycle.MotorcycleService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.sql.*;

@Service
public class YardService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private YardRepository repository;

    @Lazy
    @Autowired
    private AntennaService antennaService;

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private YardMapService yardMapService;

    public List<Yard> findAll() {
        return repository.findAll();
    }

    public List<Yard> findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public Optional<Yard> findById(Integer id) {
        return repository.findById(id);
    }

    public YardService(YardRepository repository) {
        this.repository = repository;
    }

    public Integer calcularVagas(Long idYard) {
        return repository.calcularVagasDisponiveis(idYard);
    }

    public String gerarJson(Long idYard) {
        return repository.gerarJsonYard(idYard);
    }

  //  public void gerarRelatorio() {repository.gerarRelatorioMotosPorYard();}

    public List<Map<String, Object>> gerarRelatorio() {
        List<Map<String, Object>> resultados = new ArrayList<>();

        jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement stmt = conn.prepareCall("{call PKG_RELATORIOS_PATIO.PRC_RELATORIO_MOTOS_POR_PATIO_OUT(?)}")) {
                stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    while (rs.next()) {
                        java.util.LinkedHashMap<String, Object> linha = new java.util.LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            linha.put(meta.getColumnLabel(i), rs.getObject(i));
                        }
                        resultados.add(linha);
                    }
                }
            }
            return null;
        });

        return resultados;
    }

    public void validateDuplicate(Yard yard) {
        if (yard.getZipCode() == null || yard.getNumber() == null) return;
        Optional<Yard> existing = repository.findByZipCodeAndNumber(yard.getZipCode(), yard.getNumber());
        if (existing.isPresent()) {
            Yard found = existing.get();
            if (yard.getId() == null || !found.getId().equals(yard.getId())) {
                throw new IllegalArgumentException("{service.yard.error.duplicate}" );
            }
        }
    }

    public void save(Yard yard) {
        repository.save(yard);
    }

    public void deleteById(Integer id) {
        List<Motorcycle> motorcycles = motorcycleService.findByYardId(id);
        if (motorcycles != null && !motorcycles.isEmpty()) {
            throw new IllegalStateException("{message.error.yard.deleteHasMotorcycles}" + id);
        }
        repository.deleteById(id);
    }

    public YardMapDTO prepareYardMapData(Integer yardId) {
        findById(yardId)
                .orElseThrow(() -> new IllegalArgumentException("{service.yard.error.invalidId}" + yardId));

        List<Antenna> antennas = antennaService.findByYardId(yardId);
        List<Motorcycle> motorcycles = motorcycleService.findByYardId(yardId);

        return yardMapService.createMap(antennas, motorcycles);
    }
    }