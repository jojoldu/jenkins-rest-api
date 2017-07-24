package com.github.jojoldu.jenkins;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by dwlee on 2017. 4. 27..
 */


@Getter
@NoArgsConstructor
public class JenkinsConnector {

    public String executeJob(RequestEntity requestEntity){
        final String REQUEST_URL = requestEntity.createUrl();

        try{
            Response response = createWebTarget(REQUEST_URL)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("charset", "UTF-8")
                    .header("Authorization", createCredentials(requestEntity.getUsername(), requestEntity.getToken()))
                    .post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), Response.class);

            String responseBody = response.readEntity(String.class);

            if(response.getStatus() != 200){
                throw new JenkinsExecuteException("Translate Request Exception : "+ responseBody);
            }

            return responseBody;
        }catch(Exception e){
            String message = "Jenkins Execute Exception \nRequest URL : "+REQUEST_URL+"\nException Message: "+e.getMessage();
            throw new JenkinsExecuteException(message);
        }
    }

    private WebTarget createWebTarget(String url) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        return client.target(url);
    }

    private String createCredentials(String username, String token) {
        if(StringUtils.isEmpty(username)){
            throw new JenkinsExecuteException("username is Empty");
        }

        if(StringUtils.isEmpty(token)){
            throw new JenkinsExecuteException("token is Empty");
        }

        String credentials = username+":"+token;
        String base64Credentials = new String(Base64.encodeBase64(credentials.getBytes()));
        return "Basic " + base64Credentials;
    }

}
