package br.com.sildu.ponto.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sildu.ponto.api.dtos.CadastroPJDto;
import br.com.sildu.ponto.api.entities.Empresa;
import br.com.sildu.ponto.api.entities.Funcionario;
import br.com.sildu.ponto.api.enums.PerfilEnum;
import br.com.sildu.ponto.api.response.Response;
import br.com.sildu.ponto.api.services.EmpresaService;
import br.com.sildu.ponto.api.services.FuncionarioService;
import br.com.sildu.ponto.api.utils.PasswordUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cadastrar-pj")
public class CadastroPJController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;

	/**
	 * Cadastra uma pessoa jurídica no sistema.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * @return ResponseEntity<Response<CadastroPJDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto, BindingResult result) {
		log.info("Cadastrando PJ: {}", cadastroPJDto);
		Response<CadastroPJDto> response = new Response<>();

		validarDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDto);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.empresaService.persistir(empresa);
		
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);

		response.setData(this.converterCadastroPJDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa ou funcionário já existem na base de dados.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}

	/**
	 * Converte os dados do DTO para empresa.
	 * 
	 * @param cadastroPJDto
	 * @return Empresa
	 */
	private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());

		return empresa;
	}

	/**
	 * Converte os dados do DTO para funcionário.
	 * 
	 * @param cadastroPJDto
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPJDto cadastroPJDto) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDto.getNome());
		funcionario.setEmail(cadastroPJDto.getEmail());
		funcionario.setCpf(cadastroPJDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()));

		return funcionario;
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPJDto
	 */
	private CadastroPJDto converterCadastroPJDto(Funcionario funcionario) {
		CadastroPJDto cadastroPJDto = new CadastroPJDto();
		cadastroPJDto.setId(funcionario.getId());
		cadastroPJDto.setNome(funcionario.getNome());
		cadastroPJDto.setEmail(funcionario.getEmail());
		cadastroPJDto.setCpf(funcionario.getCpf());
		cadastroPJDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());

		return cadastroPJDto;
	}

}
