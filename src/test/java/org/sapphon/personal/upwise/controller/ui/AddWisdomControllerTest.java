package org.sapphon.personal.upwise.controller.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddWisdomControllerTest {

    @Autowired
    private MockMvc mvc;

    private APIController mockApiController;

    private AddWisdomController underTest;

    private List<IWisdom> exampleWisdoms;
    private List<IVote> exampleVotes;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        mockApiController = Mockito.mock(APIController.class);

        this.underTest = new AddWisdomController(mockApiController);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

        this.exampleWisdoms = new ArrayList<>();
        exampleWisdoms.add(new WisdomJpa("A good programmer is someone who looks both ways before crossing a one-way street.", "Doug Linder", "jcrouc15", TimeLord.getNow()));
        exampleWisdoms.add(new WisdomJpa("[Javascript] doesn't exactly allow you to fall into a pit of success.", "Nick Reuter", "cshaugh1", TimeLord.getNow()));
        exampleWisdoms.add(new WisdomJpa("It's done, it just doesn't work.", "Chris Boyer", "tsatam", TimeLord.getNow()));
        exampleWisdoms.add(new WisdomJpa("May we be judged by the quality of our commits, not by the content of our Google searches.", "Connor Shaughnessy", "awalte35", TimeLord.getNow()));

        this.exampleVotes = new ArrayList<>();
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(1)));

    }

    @Test
    public void testBasicEndpointHealth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/addwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }


    @Test
    public void testGetRequestReceivesABlankWisdomOnModel() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/addwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            IWisdom actualWisdom = (IWisdom) mvcResult.getModelAndView().getModel().get("wisdomToAdd");
            assertEquals(new WisdomJpa(null,null,null,null), actualWisdom);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestWhereApiSaysBadRequestGets400Status() throws Exception {
        when(mockApiController.addWisdomEndpoint(any())).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/addwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addWisdomStatusCode");
            assertEquals(new Integer(400), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestWhereApiSaysConflictGets409Status() throws Exception {
        when(mockApiController.addWisdomEndpoint(any())).thenReturn(new ResponseEntity<>(HttpStatus.CONFLICT));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/addwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addWisdomStatusCode");
            assertEquals(new Integer(409), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }


    @Test
    public void testPostRequestWhereApiSaysCreatedGets201Status() throws Exception {
        when(mockApiController.addWisdomEndpoint(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/addwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addWisdomStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testPostRequestUsesLoggedInPrincipalsNameAsUsernameForWisdom() throws Exception {
        ArgumentCaptor<IWisdom> captor = ArgumentCaptor.forClass(IWisdom.class);
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("miriam");
        when(mockApiController.addWisdomEndpoint(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/addwisdom").accept(MediaType.TEXT_HTML).principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addWisdomStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
            verify(mockApiController).addWisdomEndpoint(captor.capture());
            assertEquals("miriam", captor.getValue().getAddedByUsername());
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testDoesNotBlowUpIfSomehowThereIsNoLoggedInPrincipalAtTimeOfWisdomSubmission() throws Exception {
        try {
            when(mockApiController.addWisdomEndpoint(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/addwisdom").accept(MediaType.TEXT_HTML))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andReturn();
            Integer actualStatusCode = (Integer) mvcResult.getModelAndView().getModel().get("addWisdomStatusCode");
            assertEquals(new Integer(201), actualStatusCode);
        } catch (Exception e) {
            Assert.fail("Should tolerate the lack of an authenticated principal.");
        }
    }
}