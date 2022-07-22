package com.example.user_service.controller;

import com.example.user_service.pojos.request.SendImageDto;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.CaretakerDelete;
import com.example.user_service.pojos.response.CaretakerListResponse;
import com.example.user_service.pojos.response.CaretakerResponse;
import com.example.user_service.service.CareTakerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@RunWith(SpringJUnit4ClassRunner.class)
class CaretakerControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @InjectMocks
    CaretakerController caretakerController;

    @Mock
    CareTakerService careTakerService;

    @Mock
    RabbitTemplate rabbitTemplate= new RabbitTemplate();

    @BeforeEach
    public void setUp(){
        this.mockMvc= MockMvcBuilders.standaloneSetup(caretakerController).build();
    }

    UserCaretakerDTO userCaretakerDTO= new UserCaretakerDTO("vinay",false,"73578dfd-e7c9-4381-a348-113e72d80fa2","548259761235609","nikunj","duyvoayvf","p");
    CaretakerResponse caretakerResponse= new CaretakerResponse();
    CaretakerListResponse caretakerListResponse= new CaretakerListResponse();

    CaretakerDelete caretakerDelete= new CaretakerDelete();

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for saving a caretaker - POST")
    void saveCaretaker() throws  Exception{
        Mockito.when(careTakerService.saveCareTaker(userCaretakerDTO)).thenReturn(caretakerResponse);
        String jsonText = objectMapper.writeValueAsString(userCaretakerDTO);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonText))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for Updating Caretaker Status - PUT")
    void updateCaretakerStatus() throws Exception{
        Mockito.when(careTakerService.updateCaretakerStatus("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(caretakerResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/accept?caretakerId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for fetching patients under me - GET")
    void getPatientsUnderMe() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/patients?caretakerId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for getting list of patient request for a caretaker - GET")
    void getPatientRequestsC() throws Exception{
        Mockito.when(careTakerService.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(caretakerListResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/patient/requests?caretakerId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for fetching list of caretakers for a patient - GET")
    void getMyCaretakers() throws Exception {
        Mockito.when(careTakerService.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(caretakerListResponse);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/caretakers?patientId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for fetching list of caretaker request for a patient - GET")
    void getCaretakerRequestsP() throws Exception{
        Mockito.when(careTakerService.getCaretakerRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(caretakerListResponse);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/caretaker/requests?patientId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for deleting a patient request - GET")
    void delPatientReq() throws Exception{
        Mockito.when(careTakerService.deletePatientRequest("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(caretakerDelete);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/delete?caretakerId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test to notify a user - GET")
    void notifyUserForMed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/notify/user?fcmToken=egufagfljbgalgfoeiugi&medicineName=PCM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    @DisplayName("Test for sending image - Multipart")
    void sendImageToCaretaker()throws Exception {
        MockMultipartFile employeeJson = new MockMultipartFile("employee", null,
                "application/json", "{\"name\": \"Emp Name\"}".getBytes());
        SendImageDto sendImageDto= new SendImageDto(employeeJson,"Vinay","PCM","73578dfd-e7c9-4381-a348-113e72d80fa2",123);
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/v1/image")
                        .file("image",sendImageDto.getImage().getBytes())
                        .param("name",sendImageDto.getName())
                        .param("medicineName",sendImageDto.getMedicineName())
                        .param("id",sendImageDto.getId())
                        .param("medicineId",sendImageDto.getMedicineId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}