package org.sapphon.upwise.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;
    private static List<String> apiStateChangingEndpoints = newArrayList("/wisdom/add", "/vote/add", "/registration/add", "/analytics/add");
    private static List<String> apiStaticEndpoints = newArrayList("/wisdom/all", "/wisdom/random", "/vote/all", "/health");

    private static List<String> uiStateChangingEndpoints = newArrayList("/addwisdom", "/addvote");
    private static List<String> uiStaticEndpoints = newArrayList("/choosenewpassword", "/choosenewpasswordABC123", "/forgotpassword", "/login", "/randomwisdom", "/viewwisdom?wisdomContent=whatever&wisdomAttribution=whatever", "/", "/user/whatever", "/wisdomleaderboard", "/recentwisdom", "/register", "/scripts/materialize-auto-init.js", "/styles/global.css");


    @Test
    public void testAPIRedirectsUnauthenticatedRequestsToPostEndpoints() throws Exception {
        checkEndpoints("POST", apiStateChangingEndpoints, MediaType.APPLICATION_JSON, status().is3xxRedirection());
    }

    @Test
    public void testAPIAcceptsUnauthenticatedRequestsToGetEndpoints() throws Exception {
        checkEndpoints("GET", apiStaticEndpoints, MediaType.APPLICATION_JSON, status().isOk());
    }

    @Test
    public void testUIRedirectsUnauthenticatedRequestsToPostEndpoints() throws Exception {
        checkEndpoints("POST", uiStateChangingEndpoints, MediaType.TEXT_HTML, status().is3xxRedirection());
    }

    @Test
    public void testUIAcceptsUnauthenticatedRequestsToGetEndpoints()  throws Exception  {
        checkEndpoints("GET", uiStaticEndpoints, MediaType.TEXT_HTML, status().isOk());
    }

    private void checkEndpoints(String method, List<String> endpoints, MediaType mediaTypesAccepted, ResultMatcher expectation) throws Exception{
        for (String endpoint : endpoints){
            mvc.perform((method.equalsIgnoreCase("post") ? MockMvcRequestBuilders.post(endpoint) : MockMvcRequestBuilders.get(endpoint)).accept(mediaTypesAccepted)).andExpect(expectation);
        }
    }

    @Test
    public void testLogoutRedirectsToLoginPageWithLoggedOutParam_ButDoesNotRequireAuth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/logout").accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login?loggedout"));
    }


}