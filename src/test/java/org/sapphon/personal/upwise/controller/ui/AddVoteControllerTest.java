package org.sapphon.personal.upwise.controller.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.Vote;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddVoteControllerTest {

    @MockBean
    private APIController apiController;

    @Autowired
    private MockMvc mvc;

    private AddVoteController underTest;
    private String urlUnderTest;

    @Before
    public void setup() {
        urlUnderTest = "addvote";
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        this.underTest = new AddVoteController(apiController);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

    }

    @Test
    public void testPostRequestWhereApiSaysBadRequestGets400Status() throws Exception{
        when(apiController.voteForWisdomEndpoint(any(), any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.BAD_REQUEST));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/"+urlUnderTest).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("statusCode");
            assertEquals(new Integer(400), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestWhereApiSaysConflictGets409Status() throws Exception{
        when(apiController.voteForWisdomEndpoint(any(), any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CONFLICT));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/"+urlUnderTest).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("statusCode");
            assertEquals(new Integer(409), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }


    @Test
    public void testPostRequestWhereApiSaysCreatedGets201Status() throws Exception{
        when(apiController.voteForWisdomEndpoint(any(), any())).thenReturn(new ResponseEntity(IVote.class, HttpStatus.CREATED));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/"+urlUnderTest).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("statusCode");
            assertEquals(new Integer(201), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }
}