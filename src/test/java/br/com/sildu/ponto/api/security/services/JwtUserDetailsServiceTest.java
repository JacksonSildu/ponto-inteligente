package br.com.sildu.ponto.api.security.services;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.sildu.ponto.api.entities.Empresa;
import br.com.sildu.ponto.api.entities.Funcionario;
import br.com.sildu.ponto.api.enums.PerfilEnum;
import br.com.sildu.ponto.api.services.FuncionarioService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class JwtUserDetailsServiceTest {

	@Autowired
	private UserDetailsService detailsService;

	@MockBean
	private FuncionarioService funcionarioService;

	@Test
	public void testUserDetailsLoadUser() {
		BDDMockito.given(this.funcionarioService.buscarPorEmail(Mockito.anyString())).willReturn(Optional.of(getFuncionario()));
		UserDetails user = detailsService.loadUserByUsername("admin@sildu.com.br");

		assertNotNull(user);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testUserDetailsLoadUserSemUsuario() {
		BDDMockito.given(this.funcionarioService.buscarPorEmail(Mockito.anyString())).willReturn(Optional.empty());
		detailsService.loadUserByUsername("admin@sildu.com.br");
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
