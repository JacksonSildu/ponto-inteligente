package br.com.sildu.ponto.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import br.com.sildu.ponto.api.dtos.FuncionarioDto;
import br.com.sildu.ponto.api.entities.Empresa;
import br.com.sildu.ponto.api.entities.Funcionario;
import br.com.sildu.ponto.api.services.FuncionarioService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class FuncionarioControllerTest {

	private static final String API_FUNCIONARIOS = "/api/funcionarios/";

	private static final String ID_FUNCIONARIO = "1";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FuncionarioService funcionarioService;

	@Test
	@WithMockUser
	public void testAtualizarFuncionario() throws Exception {
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(getFuncionario()));
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(getFuncionario());

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		FuncionarioDto dto = new FuncionarioDto();
		dto.setId(1L);
		dto.setEmail("jackson.sildu@gmail.com");
		dto.setNome("Jackson Sildu");

		mvc.perform(MockMvcRequestBuilders
				.put(API_FUNCIONARIOS + ID_FUNCIONARIO)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		dto.setSenha(Optional.of("a123456"));
		dto.setQtdHorasAlmoco(Optional.of("1"));
		dto.setQtdHorasTrabalhoDia(Optional.of("8"));
		dto.setValorHora(Optional.of("100"));
		
		mvc.perform(MockMvcRequestBuilders
				.put(API_FUNCIONARIOS + ID_FUNCIONARIO)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	public void testAtualizarFuncionarioNaoEncontrado() throws Exception {
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.empty());
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(getFuncionario());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		
		FuncionarioDto dto = new FuncionarioDto();
		dto.setId(1L);
		dto.setEmail("jackson.sildu@gmail.com");
		dto.setNome("Jackson Sildu");
		dto.setSenha(Optional.of("a123456"));
		
		mvc.perform(MockMvcRequestBuilders
				.put(API_FUNCIONARIOS + ID_FUNCIONARIO)
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}
	
	private Funcionario getFuncionario() {
		Empresa empresa = new Empresa();
		empresa.setId(1L);
		empresa.setCnpj("00000000000000");
		empresa.setDataAtualizacao(new Date());
		empresa.setDataCriacao(new Date());
		empresa.setRazaoSocial("Sildu IT");

		Funcionario funcionario = new Funcionario();
		funcionario.setCpf("06027999616");
		funcionario.setDataAtualizacao(new Date());
		funcionario.setDataCriacao(new Date());
		funcionario.setEmail("jackson.sildu@gmail.com");
		funcionario.setEmpresa(empresa);
		funcionario.setSenha("a123456");

		return funcionario;
	}

}
