package br.sergior.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import br.sergior.rest.core.BaseTest;

public class BarrigaTest extends BaseTest {
	
	protected String token;

	@Before
	public void setUpLocal() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "sergioricardojsj@gmail.com");
		login.put("senha", "127001thelz");
		
		token =	
			RestAssured.given()
				.body(login)
			.when()
				.post("/signin")
			.then()
				.statusCode(HttpStatus.SC_OK)
				.extract().path("token");
		;
	}
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED)
		;
	}
	
	@Test
	public void deveIncluirUmaContaComSucesso() {
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\": \"sergio\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Map<String, String> usuario = new HashMap<String, String>();
		usuario.put("nome", "sergioricardojsj");
		
		given()
			.header("Authorization", "JWT " + token)
			.pathParam("id", 30385)
			.body(usuario)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.body("nome", is("sergioricardojsj"))
		;
	}
	
	@Test
	public void naoDeveIncluirUmaContaComNomeRepetido() {
		Map<String, String> usuario = new HashMap<String, String>();
		usuario.put("nome", "sergioricardojsj");
		
		given()
			.header("Authorization", "JWT " + token)
			.body(usuario)
		.when()
			.post("/contas")
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(30385);
		mov.setDescricao("descricao movimentacao");
		mov.setEnvolvido("envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2000");
		mov.setData_pagamento("10/05/2010");
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.header("Authorization", "JWT " + token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(HttpStatus.SC_CREATED)
		;
	}
	
	@Test
	public void deveValidarObrigatoriedadeDosCampos() {
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.body("msg", hasItems("Data da Movimentação é obrigatório",
								   "Data do pagamento é obrigatório",
								   "Descrição é obrigatório",
								   "Interessado é obrigatório",
								   "Valor é obrigatório",
								   "Valor deve ser um número",
								   "Conta é obrigatório",
								   "Situação é obrigatório"))
		;
	}

	@Test
	public void naoDeveCadastrarMovimentacaoFutura() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(30385);
		mov.setDescricao("descricao movimentacao");
		mov.setEnvolvido("envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2020");
		mov.setData_pagamento("10/05/2020");
		mov.setValor(100f);
		mov.setStatus(true);

		given()
			.header("Authorization", "JWT " + token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.body("msg", hasItems("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}

	@Test
	public void naoDeveRemoverContaComMovimentacoes() {
		given()
			.header("Authorization", "JWT " + token)
			.pathParams("id", 30385)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}

	@Test
	public void deveCalcularSaldoDasContas() {
		List<String> saldos = given()
			.header("Authorization", "JWT " + token)
		.when()
			.get("/saldo")
		.then()
			.statusCode(HttpStatus.SC_OK).extract().path("saldo");
		;

		double somaSaldo = 0D;

		for (String s : saldos) {
			somaSaldo += Double.parseDouble(s);
		}

		assertThat(somaSaldo, equalTo(2700D));
	}

}