package br.com.sildu.ponto.api.repositories;

import java.util.List;

import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.sildu.ponto.api.entities.Lancamento;

// @formatter:off
@NamedQuery(name = "LancamentoRepository.findByFuncionarioId", query = "SELECT lanc FROM Lancamento lanc WHERE lanc.funcionario.id = :funcionarioId") 
// @formatter:on

@Transactional(readOnly = true)
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	List<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);
	Page<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId, Pageable pageable);
}
