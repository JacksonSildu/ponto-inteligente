package br.com.sildu.ponto.api.security.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import br.com.sildu.ponto.api.entities.Empresa;
import br.com.sildu.ponto.api.entities.Funcionario;
import br.com.sildu.ponto.api.enums.PerfilEnum;
import br.com.sildu.ponto.api.security.JwtUserFactory;
import br.com.sildu.ponto.api.security.dto.JwtAuthenticationDto;
import br.com.sildu.ponto.api.security.utils.JwtTokenUtil;
import br.com.sildu.ponto.api.services.EmpresaService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private EmpresaService empresaService;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private JwtTokenUtil jwtTokenUtil;

	@Test
	public void testAutenticacao() throws Exception {
		JwtAuthenticationDto dto = getDto();

		Funcionario funcionario = getFuncionario();

		BDDMockito.given(this.authenticationManager.authenticate(Mockito.any(Authentication.class))).willReturn(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));
		BDDMockito.given(this.userDetailsService.loadUserByUsername(Mockito.anyString())).willReturn(JwtUserFactory.create(funcionario));

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/auth")
				.with(httpBasic("admin@sildu.com.br", "123456"))
				.content(mapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testAutenticacaoRefresh() throws Exception {

		BDDMockito.given(this.jwtTokenUtil.tokenValido(Mockito.anyString())).willReturn(true);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/auth/refresh")
				.header("Authorization", "Bearer a12345678912345789a8sd7f98asfd98a7s9df87a9sd8f79a8sd7f98asfd98a7d9a8")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testAutenticacaoRefreshTohenInvalido() throws Exception {

		BDDMockito.given(this.jwtTokenUtil.tokenValido(Mockito.anyString())).willReturn(false);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/auth/refresh")
				.header("Authorization", "Bearer a12345678912345789a8sd7f98asfd98a7s9df87a9sd8f79a8sd7f98asfd98a7d9a8")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAutenticacaoRefreshSemBearer() throws Exception {

		BDDMockito.given(this.jwtTokenUtil.tokenValido(Mockito.anyString())).willReturn(true);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/auth/refresh")
				.header("Authorization", "a12345678912345789a8sd7f98asfd98a7s9df87a9sd8f79a8sd7f98asfd98a7d9a8")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAutenticacaoRefreshSemHeader() throws Exception {

		BDDMockito.given(this.jwtTokenUtil.tokenValido(Mockito.anyString())).willReturn(true);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		mvc.perform(MockMvcRequestBuilders
				.post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	private JwtAuthenticationDto getDto() {
		JwtAuthenticationDto dto = new JwtAuthenticationDto();
		dto.setEmail("admin@sildu.com.br");
		dto.setSenha("123456");

		return dto;
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
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);

		return funcionario;
	}
}
