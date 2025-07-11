package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.Location;
import org.hamcrest.Matchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.Student;
import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Location.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.oauth2.jwt.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class SecurityTests {
    @Autowired
    private MockMvc mockMvc;

    private static final String REQUEST_ENDPOINT = "/api/oauth/token";
    private static final String LIST_STUDENT_ENDPOINT = "/api/students";
    private static final String LOCATION_ENDPOINT="/v1/locations";

    @Autowired
    ObjectMapper objectMapper;
//    @Autowired
//    AuthenticationManager manager;

    @Autowired
    StudentRepository studentRepository;

    @MockitoBean
    TokenService token;

    @Autowired
    private JwtUtility jwtUtility;

    @BeforeEach
    public void setup()
    {
        jwtUtility.setIssuerName("My Company");
        jwtUtility.setAccessTokenExpiration(2);
        jwtUtility.setSecretKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnoqrstuv+9-@$%#&%");

    }

    @Test
    public void testGetBaseUriShouldReturn401() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAccessTokenBadRequest() throws Exception{
        AuthRequest request=AuthRequest.builder()
                .username("sophieeeeeeeeeeeeeeee")
                .password("test123")
                .build();
        mockMvc.perform(post(REQUEST_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccessTokenFail() throws Exception{
        AuthRequest request=AuthRequest.builder()
                .username("admin")
                .password("aaaaa")
                .build();
        mockMvc.perform(post(REQUEST_ENDPOINT)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAccessTokeSuccess() throws Exception{
        AuthRequest request=AuthRequest.builder()
                .username("admin")
                .password("admin")
                .build();
         String accessToken="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLGFkbWluIiwiaXNzIjoiTXkgQ29tcGFueSIsImlhdCI6MTc1MTg4NzIzMiwiZXhwIjoxNzUxODg3MzUyLCJyb2xlIjoid3JpdGUifQ.9oV3P8g0d56bKOpUK4WS8AdRlfRvU8xchx8NMMvvYdcyrUGJZI1uk7i_IVU2cyaSjRmN6ImsHd_5p0H1htCMOw";
         String refreshToken=UUID.randomUUID().toString();
         AuthResponse response= AuthResponse
                 .builder()
                 .accessToken(accessToken)
                 .refreshToken(refreshToken)
                 .build();
        when(token.generateToken(any(User.class))).thenReturn(response);
        mockMvc.perform(post(REQUEST_ENDPOINT)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void testGetStudentListFail() throws Exception{
        mockMvc.perform(get(LIST_STUDENT_ENDPOINT).header("Authorization","Bearer somethingrandominvalid"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetStudentListSuccess() throws Exception{
        AuthRequest authRequest=AuthRequest.builder()
                        .username("admin")
                        .password("admin")
                        .build();
        String accessToken="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLGFkbWluIiwiaXNzIjoiTXkgQ29tcGFueSIsImlhdCI6MTc1MTc1NjY1MCwiZXhwIjoxNzUxNzU2NzcwLCJyb2xlIjoid3JpdGUifQ.bXyeGVRIjo1FupEs_qOUwY6AwOdA3p9LoFBYYittOmaCbRNIuK0riQq6gRrFPvgO_LepO7v5WHrw6ENErhBjJA";
        String refreshToken=UUID.randomUUID().toString();
        AuthResponse response= AuthResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        User user= User.builder()
                .id(2)
                .username("admin")
                .password("admin")
                .role("write")
                .build();
        when(token.generateToken(argThat(u ->
                u.getUsername().equals("admin") &&
                        u.getRole().equals("write")
        ))).thenReturn(response);
        MvcResult result=mockMvc.perform(post(REQUEST_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        String responseBody=result.getResponse().getContentAsString();
        AuthResponse authResponse=objectMapper.readValue(responseBody, AuthResponse.class);
        String token = "Bearer " + authResponse.getAccessToken();
        when(studentRepository.findAll())
                .thenReturn(List.of(new Student(1, "kevin"),new Student(2, "sophie")));
        mockMvc.perform(get(LIST_STUDENT_ENDPOINT)
                .param("pageSize", "10")
                .param("pageNum", "1")
                .header("Authorization",token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].id").isNumber());
    }

    @Test
    public void testAddStudent1() throws Exception {

        String apiEndpoint="/api/students";
        Student student=new Student();
        student.setName("sophie sung");
        student.setId(1);
       String requestBody=objectMapper.writeValueAsString(student);
       mockMvc.perform(post(apiEndpoint)
               .contentType("application/json")
               .content(requestBody).with(jwt().authorities(new SimpleGrantedAuthority("write"))))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name").isString())
               .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testAddStudent2() throws Exception {

        String apiEndpoint="/api/students";
        Student student=new Student();
        student.setName("kevin hung");
        student.setId(2);
        String requestBody=objectMapper.writeValueAsString(student);
        mockMvc.perform(post(apiEndpoint)
                        .contentType("application/json")
                        .content(requestBody).with(jwt().jwt(jwt -> jwt.claim("scope","write"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testUpdateStudent1() throws Exception {

        String apiEndpoint="/api/students";
        Student student=new Student();
        student.setName("Josh");
        student.setId(2);
        String requestBody=objectMapper.writeValueAsString(student);
        var jwt=Jwt.withTokenValue("xxxx")
                .header("alg","none")
                        .issuer("My Company")
                .claim("scope","write")
                .subject("2,kevin")
                .issuedAt(new Date().toInstant()).build()
        ;

        var authorities= AuthorityUtils.createAuthorityList("SCOPE_write");
        var token=new JwtAuthenticationToken(jwt,authorities);;
        mockMvc.perform(put(apiEndpoint)
                        .contentType("application/json")
                        .content(requestBody).with(authentication(token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testDelete() throws Exception {
        Integer studentId=2;
        String apiEndpoint="/api/students/"+studentId;
        mockMvc.perform(delete(apiEndpoint)
                        .with(jwt().authorities(new SimpleGrantedAuthority("write"))))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetBaseURI() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT)
                        .with(jwt().jwt(jwt-> jwt.claim("scope","READER"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testListLocationsWithScopeReader() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT)
                        .with(jwt().jwt(jwt-> jwt.claim("scope","READER"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testListLocationsWithUnknown() throws Exception {
        mockMvc.perform(get(LOCATION_ENDPOINT)
                        .with(jwt().jwt(jwt-> jwt.claim("scope","USER"))))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdLocationsWithScopeReader() throws Exception {

        Location location= new Location("test","test","test","test");
        mockMvc.perform(post(LOCATION_ENDPOINT)
                        .contentType("applciation/json")
                        .content(objectMapper.writeValueAsString(location))
                        .with(jwt().jwt(jwt-> jwt.claim("scope","READER"))))
                .andDo(print())
                .andExpect(status().isForbidden());
    }














}


