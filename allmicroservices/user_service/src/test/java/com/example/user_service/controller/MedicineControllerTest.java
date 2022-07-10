package com.example.user_service.controller;

import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Messages;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
class MedicineControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @InjectMocks
    private MedicineController medicineController;

    @Mock
    UserMedicineService userMedicineService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        this.mockMvc= MockMvcBuilders.standaloneSetup(medicineController).build();
    }

    UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
    MedicinePojo medicinePojo= new MedicinePojo(123,"Mon",1,null,"something",10,"PCM","something",null,0,"10:00 AM");
    List<MedicinePojo> medicinePojoList = new ArrayList<>();



    @Test
    @ExtendWith(MockitoExtension.class)
    void syncData() throws  Exception{
        medicinePojoList.add(medicinePojo);
        Mockito.when(userMedicineService.syncData("73578dfd-e7c9-4381-a348-113e72d80fa2",medicinePojoList)).thenReturn(Messages.SUCCESS);
        String jsonText= objectMapper.writeValueAsString(medicinePojoList);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/medicines/sync?userId=73578dfd-e7c9-4381-a348-113e72d80fa2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonText))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void syncMedicineHistory() throws Exception{
        String[] timing= new String[4];
        timing[0]="10:00 AM";
        timing[1]="12:00 PM";
        MedicineHistoryDTO medicineHistoryDTO= new MedicineHistoryDTO(1234,"12 June", timing,timing);
        MedicineHistoryDTO medicineHistoryDTO1= new MedicineHistoryDTO(1235,"12 June",timing,timing);
        MedicineHistoryDTO medicineHistoryDTO2= new MedicineHistoryDTO(1236,"12 June",timing,timing);
        List<MedicineHistoryDTO> medicineHistoryDTOList= new ArrayList<>();
        medicineHistoryDTOList.add(medicineHistoryDTO);
        medicineHistoryDTOList.add(medicineHistoryDTO1);
        medicineHistoryDTOList.add(medicineHistoryDTO2);
        String jsonText= objectMapper.writeValueAsString(medicineHistoryDTOList);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/medicine-history/sync?medId=123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonText))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getMedicineHistories() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/medicine-histories?medId=123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void getMedicineImages() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/medicine-images?medId=123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}