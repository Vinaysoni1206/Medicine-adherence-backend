package com.example.user_service.service.caretaker;

import com.example.user_service.exception.caretaker.UserCaretakerException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.SendImageDto;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.image.ImageResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponsePage;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserCaretakerRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.util.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CareTakerServiceTest {

    CareTakerServiceImpl careTakerServiceImpl;

    @Mock
    UserCaretakerRepository userCaretakerRepository;

    @Mock
    ModelMapper mapper;

    @Mock
    ImageRepository imageRepository;

    @Mock
    UserMedicineRepository userMedicineRepository;
    @Mock
    RabbitTemplate rabbitTemplate;

    @BeforeEach
    void init(){
        careTakerServiceImpl = new CareTakerServiceImpl(imageRepository,userMedicineRepository,rabbitTemplate,userCaretakerRepository,mapper);
    }

    @Test
    void saveCaretakerException(){
        UserCaretakerDTO userCaretakerDTO= new UserCaretakerDTO();
        UserCaretaker userCaretaker= new UserCaretaker();
        when(mapper.map(userCaretakerDTO,UserCaretaker.class)).thenReturn(userCaretaker);
        when(userCaretakerRepository.check(userCaretaker.getPatientId(),userCaretaker.getCaretakerId())).thenReturn(userCaretaker);
        try{
            careTakerServiceImpl.saveCareTaker(userCaretakerDTO);
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.CARETAKER_ALREADY_PRESENT,userCaretakerException.getMessage());
        }
    }

    @Test
    void saveCaretakerTest() throws UserCaretakerException {
        UserCaretakerDTO userCaretakerDTO= new UserCaretakerDTO();
        userCaretakerDTO.setPatientName("vinay");
        UserCaretaker userCaretaker= new UserCaretaker();
        when(mapper.map(userCaretakerDTO,UserCaretaker.class)).thenReturn(userCaretaker);
        when(userCaretakerRepository.check(userCaretaker.getPatientId(),userCaretaker.getCaretakerId())).thenReturn(null);
        UserCaretaker userCaretaker1= careTakerServiceImpl.saveCareTaker(userCaretakerDTO);
        Assertions.assertEquals(userCaretaker.getPatientName(),userCaretaker1.getPatientName());
    }

    @Test
    void updateCaretakerStatusException(){
        UserCaretaker userCaretaker= new UserCaretaker();
        when(userCaretakerRepository.getById(userCaretaker.getCId())).thenReturn(userCaretaker);
        try{
            careTakerServiceImpl.updateCaretakerStatus(userCaretaker.getCId());
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.NO_RECORD_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void updateCaretakerStatusTest() throws UserCaretakerException {
        UserCaretaker userCaretaker= new UserCaretaker("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay",true,"ftdutsaduifioadf","yfuydfafakjfdafdou","nikunj",LocalDateTime.now(),"p");
        when(userCaretakerRepository.getById(userCaretaker.getCId())).thenReturn(userCaretaker);
        UserCaretaker userCaretaker1= careTakerServiceImpl.updateCaretakerStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretaker.getCaretakerId(),userCaretaker1.getCaretakerId());
    }

    @Test
    void getPatientsUnderMeException(){
        int pageNo=0;
        int pageSize = 2;
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        UserCaretaker userCaretaker= new UserCaretaker();
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        Page<UserCaretaker> userCaretakerPage= new PageImpl<>(userCaretakerList);
        when(userCaretakerRepository.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageable)).thenReturn(userCaretakerPage);
        try{
            careTakerServiceImpl.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageNo,pageSize);
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userCaretakerException.getMessage());
        }
    }


    @Test
    void getPateientsUnderMeTest() throws UserCaretakerException {
        int pageNo=0;
        int pageSize = 2;
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        UserCaretaker userCaretaker= new UserCaretaker();
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        Page<UserCaretaker> userCaretakerPage= new PageImpl<>(userCaretakerList);
        CaretakerResponsePage caretakerResponsePage= new CaretakerResponsePage(Messages.SUCCESS,Messages.DATA_FOUND,5L,2,0,userCaretakerList);
        when(userCaretakerRepository.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageable)).thenReturn(userCaretakerPage);
        CaretakerResponsePage caretakerResponsePage1= careTakerServiceImpl.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageNo,pageSize);
        Assertions.assertEquals(caretakerResponsePage.getUserCaretaker().size(),caretakerResponsePage1.getUserCaretaker().size());
    }

    @Test
    void getPatientRequestException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getPatientRequestTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        List<UserCaretaker> userCaretakerList1 = careTakerServiceImpl.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),userCaretakerList1.size());
    }

    @Test
    void getMyCaretakersException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getMyCaretakersTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        List<UserCaretaker> userCaretakerList1 = careTakerServiceImpl.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),userCaretakerList1.size());
    }

    @Test
    void getCaretakerRequestStatusException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.NO_RECORD_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getCaretakerRequestStatusTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        List<UserCaretaker> userCaretakerList1 = careTakerServiceImpl.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),userCaretakerList1.size());
    }

    @Test
    void getCaretakerRequestPException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getCaretakerRequestsP("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getCaretakerRequestsP("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getCaretakerRequestPTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getCaretakerRequestsP("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        List<UserCaretaker> userCaretakerList1 = careTakerServiceImpl.getCaretakerRequestsP("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),userCaretakerList1.size());
    }

    @Test
    void delPatientReqException() {
        Optional<UserCaretaker> userCaretaker= Optional.empty();
        when(userCaretakerRepository.findById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretaker);
        try{careTakerServiceImpl.delPatientReq("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException | UserExceptionMessage userCaretakerException){
            Assertions.assertEquals(Messages.NO_RECORD_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void delPatientReqTest() throws UserExceptionMessage, UserCaretakerException {
        UserCaretaker userCaretaker= new UserCaretaker("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay",true,"ftdutsaduifioadf","yfuydfafakjfdafdou","nikunj",LocalDateTime.now(),"p");
        Optional<UserCaretaker> userCaretakerTest= Optional.of(userCaretaker);
        when(userCaretakerRepository.findById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerTest);
        String text =careTakerServiceImpl.delPatientReq("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(Messages.SUCCESS,text);
    }

    @Test
    void sendImageToCaretakerException() throws UserCaretakerException {
        MockMultipartFile employeeJson = new MockMultipartFile("employee", null,
                "application/json", "{\"name\": \"Emp Name\"}".getBytes());
        UserMedicines userMedicines= new UserMedicines();
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImageResponse imageResponse= careTakerServiceImpl.sendImageToCaretaker(employeeJson,"fyiaifkvaf","73578dfd-e7c9-4381-a348-113e72d80fa2","PCM",123);
        Assertions.assertEquals(Messages.FAILED,imageResponse.getStatus());

    }



    @Test
    void sendImageToCaretakerTest() throws UserCaretakerException{
        MockMultipartFile employeeJson = new MockMultipartFile("employee", null,
                "application/json", "{\"name\": \"Emp Name\"}".getBytes());
        SendImageDto sendImageDto= new SendImageDto(employeeJson,"Vinay","PCM","73578dfd-e7c9-4381-a348-113e72d80fa2",123);
        UserEntity user= new UserEntity();
        List<MedicineHistory> medicineHistoryList= Collections.emptyList();
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImageResponse imageResponseTest = careTakerServiceImpl.sendImageToCaretaker(sendImageDto.getImage(),"Something","73578dfd-e7c9-4381-a348-113e72d80fa2","PCM",123);
        Assertions.assertEquals(Messages.SUCCESS,imageResponseTest.getStatus());
    }


}
