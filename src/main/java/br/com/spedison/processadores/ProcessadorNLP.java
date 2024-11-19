package br.com.spedison.processadores;

import br.com.spedison.config.ProcessadorNLPConfig;
import br.com.spedison.processadores.dto.ResultadoLematizador;
import br.com.spedison.processadores.dto.ResultadoRadical;
import br.com.spedison.processadores.dto.ResultadoTokens;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ProcessadorNLP {

    private static Logger log = LoggerFactory.getLogger(ProcessadorNLP.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ProcessadorNLPConfig config = new ProcessadorNLPConfig();

    private String makeJson(String paginaCompleta) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("text", paginaCompleta);
        try {
            return objectMapper.writeValueAsString(requestData);
        } catch (JsonProcessingException e) {
            log.error("Problemas na geração do JSON: " + e.getMessage(), e);
            return null;
        }
    }

    public ResultadoTokens getTokensParagrafo(String paginaCompleta) {
        log.trace("Processando página [[%s]]".formatted(paginaCompleta));
        HttpPost postRequest = new HttpPost(config.getUrlToken());
        try (CloseableHttpClient httpClient = criaHttpClient(paginaCompleta, postRequest)) {
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ResultadoTokens responseData = objectMapper.readValue(response.getEntity().getContent(), ResultadoTokens.class);
                log.trace("JSON retornado: %s".formatted(responseData));
                return responseData;
            } catch (IOException e) {
                log.error("Erro no envio dos dados:" + e.getMessage(), e);
                return null;
            }
        } catch (IOException e) {
            log.error("Erro ao criar Client Default : " + e.getMessage(), e);
            return null;
        }
    }

    public ResultadoLematizador getLemanizacaoComRetry(String texto) {
        return getLemanizacaoComRetry(texto,config.getQuantidadeDeRepeticoes());
    }

    public ResultadoLematizador getLemanizacaoComRetry(String texto, int limiteDeVezes) {
        ResultadoLematizador lematizacao;
        do {
            lematizacao = getLemanizacao(texto);
            if (Objects.nonNull(lematizacao) &&
                    Objects.nonNull(lematizacao.getEstado()) &&
                    !lematizacao.getEstado().isBlank()) {
                return lematizacao;
            }
            try {
                Random rn = new Random();
                Thread.sleep(1 + Math.round(rn.nextDouble() * 7.));
            } catch (InterruptedException ioe) {
                ioe.printStackTrace();
                log.error("Problema enquanto aguarda o retry : " + ioe.getMessage(),ioe);
                continue;
            }
        } while (
                (limiteDeVezes--) > 0 &&
                        (
                                Objects.isNull(lematizacao) ||
                                        Objects.isNull(lematizacao.getLematizado()) ||
                                        lematizacao.getLematizado().isEmpty()
                        ));
        return new ResultadoLematizador(texto,texto,"Erro ao tentar enviada para o servidor.");
    }

    public ResultadoLematizador getLemanizacao(String texto) {
        log.trace("Lemanizando texto [[%s]]".formatted(texto));
        HttpPost postRequest = new HttpPost(config.getUrlLemanizacao());
        try (CloseableHttpClient httpClient = criaHttpClient(texto, postRequest)) {
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ResultadoLematizador responseData = objectMapper.readValue(response.getEntity().getContent(), ResultadoLematizador.class);
                log.trace("JSON retornado: %s".formatted(responseData));
                return responseData;
            } catch (IOException e) {
                log.error("Erro no envio dos dados:" + e.getMessage(), e);
                return null;
            }
        } catch (IOException e) {
            log.error("Erro ao criar Client Default : " + e.getMessage(), e);
            return null;
        }
    }

    public ResultadoRadical getRadical(String texto) {
        log.trace("Stem texto [[%s]]".formatted(texto));
        HttpPost postRequest = new HttpPost(config.getUrlRadical());
        try (CloseableHttpClient httpClient = criaHttpClient(texto, postRequest)) {
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ResultadoRadical responseData = objectMapper.readValue(response.getEntity().getContent(), ResultadoRadical.class);
                log.trace("JSON retornado: %s".formatted(responseData));
                return responseData;
            } catch (IOException e) {
                log.error("Erro no envio dos dados:" + e.getMessage(), e);
                return null;
            }
        } catch (IOException e) {
            log.error("Erro ao criar Client Default : " + e.getMessage(), e);
            return null;
        }
    }


    private CloseableHttpClient criaHttpClient(String texto, HttpPost postRequest) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Accept", " text/json");
        postRequest.setHeader("Accept-Encoding", "br, deflate, gzip, x-gzip");

        String jsonBody = makeJson(texto);
        if (Objects.isNull(jsonBody))
            return null;

        postRequest.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        return httpClient;
    }

    public ResultadoTokens getTokensParagrafoComRetry(String conteudo) {
        return getTokensParagrafoComRetry(conteudo, config.getQuantidadeDeRepeticoes());
    }

    public ResultadoTokens getTokensParagrafoComRetry(String conteudo, int limiteExecucoes) {

        if (conteudo.isBlank()){
            log.trace("Conteúdo do paragrafo é vazio.");
            return new ResultadoTokens(conteudo, List.of(""),1,"Conteudo Paragrafo Vazio");
        }

        ResultadoTokens tokens;
        do {
            tokens = getTokensParagrafo(conteudo);
            if (Objects.nonNull(tokens) &&
                    Objects.nonNull(tokens.getTokens()) &&
                   !tokens.getTokens().isEmpty()) {
                return tokens;
            }
            try {
                Random rn = new Random();
                Thread.sleep(1 + Math.round(rn.nextDouble() * 7.));
            } catch (InterruptedException ioe) {

            }
        } while (
                (limiteExecucoes--) > 0 &&
                        (
                                Objects.isNull(tokens) ||
                                        Objects.isNull(tokens.getTokens()) ||
                                        tokens.getTokens().isEmpty()
                        ));
        return new ResultadoTokens(conteudo, Arrays.stream(conteudo.split("\n")).toList(),-1,"Erro ao tentar enviar a paragrafo para o servidor.");
    }
}