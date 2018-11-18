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

import br.com.sildu.ponto.api.dtos.CadastroPFDto;
import br.com.sildu.ponto.api.entities.Empresa;
import br.com.sildu.ponto.api.entities.Funcionario;
import br.com.sildu.ponto.api.services.EmpresaService;
import br.com.sildu.ponto.api.services.FuncionarioService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CadastroPFControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FuncionarioService funcionarioService;

	@MockBean
	private EmpresaService empresaService;

	@Test
	@WithMockUser
	public void testCadastrarPF() throws Exception {
		Funcionario funcionario = getFuncionario();
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.of(new Empresa()));

		CadastroPFDto dto = getDto();

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/api/cadastrar-pf/")
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		dto.setQtdHorasAlmoco(Optional.of("1"));
		dto.setQtdHorasTrabalhoDia(Optional.of("8"));
		funcionario.setQtdHorasAlmoco(1F);
		funcionario.setQtdHorasTrabalhoDia(8F);
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
		

		mvc.perform(MockMvcRequestBuilders
				.post("/api/cadastrar-pf/")
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void testCadastrarPFEmpresaNaoEncotranda() throws Exception {
		Funcionario funcionario = getFuncionario();
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.empty());

		CadastroPFDto dto = getDto();

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/api/cadastrar-pf/")
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
	public void testCadastrarPFCPFJaCadastrado() throws Exception {
		Funcionario funcionario = getFuncionario();
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.of(new Empresa()));
		BDDMockito.given(this.funcionarioService.buscarPorCpf(Mockito.anyString())).willReturn(Optional.of(new Funcionario()));

		CadastroPFDto dto = getDto();

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/api/cadastrar-pf/")
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
	public void testCadastrarPFEmailJaCadastrado() throws Exception {
		Funcionario funcionario = getFuncionario();
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.of(new Empresa()));
		BDDMockito.given(this.funcionarioService.buscarPorEmail(Mockito.anyString())).willReturn(Optional.of(new Funcionario()));

		CadastroPFDto dto = getDto();

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/api/cadastrar-pf/")
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

	private CadastroPFDto getDto() {
		CadastroPFDto dto = new CadastroPFDto();
		dto.setId(1L);
		dto.setCpf("06027999616");
		dto.setEmail("jackson.sildu@gmail.com");
		dto.setNome("Jackson Sildu");
		dto.setSenha("a123456");
		dto.setValorHora(Optional.of("100"));
		dto.setCnpj("00000000000000");

		return dto;
	}

}
