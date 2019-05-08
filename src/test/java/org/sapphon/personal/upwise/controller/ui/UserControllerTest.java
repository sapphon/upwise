package org.sapphon.personal.upwise.controller.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.model.datatransfer.PasswordResetRequest;
import org.sapphon.personal.upwise.model.datatransfer.UserRegistration;
import org.sapphon.personal.upwise.presentation.WisdomPresentation;
import org.sapphon.personal.upwise.service.UserService;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sapphon.personal.upwise.TestHelper.assertIsOfTypeAndGet;
import static org.sapphon.personal.upwise.TestHelper.assertListEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    private WisdomService wisdomService;

    private VoteService voteService;

    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private UserController underTest;

    private List<IWisdom> exampleWisdoms;
    private List<IVote> exampleVotes;
    private APIController mockApiController;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");
        mockApiController = Mockito.mock(APIController.class);
        voteService = Mockito.mock(VoteService.class);
        wisdomService = Mockito.mock(WisdomService.class);
        userService = Mockito.mock(UserService.class);

        this.underTest = new UserController(wisdomService, voteService, mockApiController, userService);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

        this.exampleWisdoms = new ArrayList<>();
        exampleWisdoms.add(new Wisdom("A good programmer is someone who looks both ways before crossing a one-way street.", "Doug Linder", "jcrouc15", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("[Javascript] doesn't exactly allow you to fall into a pit of success.", "Nick Reuter", "cshaugh1", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("It's done, it just doesn't work.", "Chris Boyer", "tsatam", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("May we be judged by the quality of our commits, not by the content of our Google searches.", "Connor Shaughnessy", "awalte35", TimeLord.getNow()));

        this.exampleVotes = new ArrayList<>();
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(1)));

    }

    @Test
    public void testCollaboratesWithWisdomServiceAndVoteService() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/testBoi").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(wisdomService, times(1)).getAllWisdomsBySubmitter("testBoi");
        verify(voteService, times(1)).getAllByVoter("testBoi");
        verify(userService, times(1)).getUserWithLogin("testBoi");
    }

    @Test
    public void testSetsCorrectValuesOnModel() throws Exception {
        IUser testBoi = RandomObjectFactory.makeRandomUser().setLoginUsername("testBoi");
        List<IWisdom> wisdomsVotedForByTestBoi = this.exampleVotes.stream().map(IVote::getWisdom).collect(Collectors.toList());
        List<IWisdom> wisdomsSubmittedByTestBoi = this.exampleWisdoms;

        when(userService.getUserWithLogin("testBoi")).thenReturn(testBoi);
        when(wisdomService.getAllWisdomsBySubmitter("testBoi")).thenReturn(wisdomsSubmittedByTestBoi);
        when(voteService.getAllByVoter("testBoi")).thenReturn(this.exampleVotes);
        when(wisdomService.getWisdomPresentationsForWisdoms(wisdomsSubmittedByTestBoi)).thenReturn(wisdomsSubmittedByTestBoi.stream().map(RandomObjectFactory::makeWisdomPresentationFor).collect(Collectors.toList()));
        when(wisdomService.getWisdomPresentationsForWisdoms(wisdomsVotedForByTestBoi)).thenReturn(wisdomsVotedForByTestBoi.stream().map(RandomObjectFactory::makeWisdomPresentationFor).collect(Collectors.toList()));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/user/testBoi").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            String actualUsername = (String) mvcResult.getModelAndView().getModel().get("userLoginName");
            String actualDisplayName = (String) mvcResult.getModelAndView().getModel().get("userDisplayName");
            List<WisdomPresentation> actualSubmittedWisdoms = assertIsOfTypeAndGet(mvcResult.getModelAndView().getModel().get("allWisdomsSubmitted"));
            List<WisdomPresentation> actualVotedWisdoms = assertIsOfTypeAndGet(mvcResult.getModelAndView().getModel().get("allWisdomsVotedFor"));
            assertEquals(testBoi.getLoginUsername(), actualUsername);
            assertEquals(testBoi.getDisplayName(), actualDisplayName);
            assertListEquals(this.exampleWisdoms, actualSubmittedWisdoms);
            assertListEquals(wisdomsVotedForByTestBoi, actualVotedWisdoms);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    @Test
    public void testCanGetRegistrationPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/register").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    public void testCanGetLoginPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }


    @Test
    public void testCanGetResetPasswordPage() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/forgotpassword").accept(MediaType.TEXT_HTML)).andExpect(status().isOk()).andReturn();
        assertEquals("forgotpassword", mvcResult.getModelAndView().getViewName());
        PasswordResetRequest passwordResetRequest = (PasswordResetRequest) mvcResult.getModelAndView().getModel().get("passwordResetRequest");
        assertEquals(new PasswordResetRequest().getEmail(), passwordResetRequest.getEmail());
    }

    @Test
    public void testLoginErrorSets400StatusCodeOnModel() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/login").param("error", "").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
        assertEquals(400, result.getModelAndView().getModel().get("loginStatusCode"));
    }

    @Test
    public void testLogoutSets200StatusCodeOnModel() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/login").param("loggedout", "").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
        assertEquals(200, result.getModelAndView().getModel().get("logoutStatusCode"));
    }

    @Test
    public void testSubmittingTheResetPasswordFormShowsYouTheResetPasswordLandingPage() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/forgotpassword").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("forgotpasswordresult", mvcResult.getModelAndView().getViewName());

    }


    @Test
    public void testRegistrationErrorSets400StatusCodeOnModel_AndSendsYouToTryAgain() throws Exception {
        when(mockApiController.addUserEndpoint(any())).thenReturn(new ResponseEntity(IUser.class, HttpStatus.BAD_REQUEST));
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/register").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
        assertEquals(400, result.getModelAndView().getModel().get("registrationStatusCode"));
        assertEquals("register", result.getModelAndView().getViewName());
    }

    @Test
    public void testRegistrationSuccessSets201StatusCodeOnModel_AndRedirectsToUserPage() throws Exception {
        User expectedUser = new User("aaa", "bbb", TimeLord.getNow(), "ccc", "ddd");
        when(mockApiController.addUserEndpoint(any())).thenReturn(new ResponseEntity<IUser>(expectedUser, HttpStatus.CREATED));
        when(userService.getUserWithLogin(any())).thenReturn(expectedUser);
            MvcResult result = mvc.perform(addRequestAttributesForUserRegistration(RandomObjectFactory.makeValidButRandomUserRegistration(), MockMvcRequestBuilders.post("/register").accept(MediaType.TEXT_HTML)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(equalTo("")))
                    .andReturn();

        assertEquals(201, result.getModelAndView().getModel().get("registrationStatusCode"));
        assertEquals("userdashboard", result.getModelAndView().getViewName());
    }

    @Test
    public void testRegistrationConflictSets409StatusCodeOnModel_AndSendsYouToTryAgain() throws Exception {
        when(mockApiController.addUserEndpoint(any())).thenReturn(new ResponseEntity(IUser.class, HttpStatus.CONFLICT));
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/register").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")))
                .andReturn();
        assertEquals(409, result.getModelAndView().getModel().get("registrationStatusCode"));
        assertEquals("register", result.getModelAndView().getViewName());
    }

    private MockHttpServletRequestBuilder addRequestAttributesForUserRegistration(UserRegistration reg, MockHttpServletRequestBuilder requestBuilder){
        return requestBuilder
                .param("desiredUsername", reg.getDesiredUsername())
                .param("displayName", reg.getDisplayName())
                .param("password", reg.getPassword())
                .param("confirmPassword", reg.getConfirmPassword());
    }

    @Test
    public void testWhenPasswordResetFormSubmitted_DoesNotSendEmailIfAccountNotFound() throws Exception {
        when(userService.hasUserWithEmail(any())).thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.post("/forgotpassword").accept(MediaType.TEXT_HTML)
                .flashAttr("passwordResetRequest", createPasswordResetRequestObject("notapresent@email"))
        ).andExpect(status().isOk());
        verify(userService, times(0)).enablePasswordResetForUser(any());
    }

    private PasswordResetRequest createPasswordResetRequestObject(String email) {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail(email);
        return passwordResetRequest;
    }

    @Test
    public void testWhenPasswordResetFormSubmitted_EnablePasswordResetIsInvokedOnUserServiceWithThatEmail() throws Exception {
        when(userService.hasUserWithEmail("expected@email")).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.post("/forgotpassword").accept(MediaType.TEXT_HTML)
                .flashAttr("passwordResetRequest", createPasswordResetRequestObject("expected@email"))
        ).andExpect(status().isOk());
        verify(userService, times(1)).enablePasswordResetForUser("expected@email");
    }

}