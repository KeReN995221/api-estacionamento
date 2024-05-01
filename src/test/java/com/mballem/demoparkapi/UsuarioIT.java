package com.mballem.demoparkapi;

import com.mballem.demoparkapi.web.dto.UsuarioCreateDto;
import com.mballem.demoparkapi.web.dto.UsuarioResponseDto;
import com.mballem.demoparkapi.web.dto.UsuarioSenhaDto;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {
    @Autowired
    WebTestClient webClient;

    @Test
    public void createUsuario_ComUsernameEPasswordValidos_RetornarUsuarioCriadoComStatus201() {
        UsuarioResponseDto responseBody = webClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("keren@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("keren@gmail.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }


    @Test
    public void createUsuario_UsernamePassworInvValidos_RetornarErrorMessageStatus422() {
        ErrorMessage errorMessage =  webClient.post().uri("/api/v1/users").contentType(MediaType.APPLICATION_JSON).bodyValue(new UsuarioCreateDto("", "123456")).exchange().expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(errorMessage).isNotNull();
        org.assertj.core.api.Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);

        errorMessage =  webClient.post().uri("/api/v1/users").contentType(MediaType.APPLICATION_JSON).bodyValue(new UsuarioCreateDto("keren@", "123456")).exchange().expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(errorMessage).isNotNull();
        org.assertj.core.api.Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);


        errorMessage =  webClient.post().uri("/api/v1/users").contentType(MediaType.APPLICATION_JSON).bodyValue(new UsuarioCreateDto("keren@email", "123456")).exchange().expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(errorMessage).isNotNull();
        org.assertj.core.api.Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);

    }

    @Test
    public void createUsuario_ComPasswordInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responseBody = webClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("keren@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("keren@gmail.com", "123"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("keren@gmail.com", "123456789"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComUsernameRepetido_RetornarErrorMessageComStatus409() {
        ErrorMessage responseBody = webClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ana@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }


    @Test
    public void buscarUsuario_IdExistente_RetornarUsuarioCriadoStatus200() {
        UsuarioResponseDto usuarioResponseDto =  webClient.get().uri("/api/v1/users/100").exchange()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto.getUsername()).isEqualTo("ana@gmail.com");
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto.getRole()).isEqualTo("CLIENTE");


    }

    @Test
    public void buscarUsuariosExistentes_RetornarUsuariosCriadoStatus200() {
       List<UsuarioResponseDto> responseBody = webClient.get().uri("/api/v1/users").exchange()
                .expectBodyList(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

       org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
       org.assertj.core.api.Assertions.assertThat(responseBody.size()).isEqualTo(3);
    }

    @Test
    public void buscarUsuario_Idinexistente_RetornarErrorMessagetatus200() {
        ErrorMessage usuarioResponseDto =  webClient.get().uri("/api/v1/users/0").exchange().expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDto.getStatus()).isEqualTo(404);


    }


    @Test
    public void editarSenha_dadosValidos_RetornarUsuarioCriadoStatus204() {
        webClient.patch().uri("/api/v1/users/100").contentType(MediaType.APPLICATION_JSON).bodyValue(new UsuarioSenhaDto("123456", "123456", "123456")).exchange().expectStatus().isNoContent();
    }
}
