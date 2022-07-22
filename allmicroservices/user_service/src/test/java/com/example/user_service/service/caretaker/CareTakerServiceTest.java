package com.example.user_service.service.caretaker;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.MedicineHistory;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.SendImageDto;
import com.example.user_service.pojos.request.UserCaretakerDTO;
import com.example.user_service.pojos.response.*;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserCaretakerRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.service.impl.CareTakerServiceImpl;
import com.example.user_service.util.Constants;
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
            Assertions.assertEquals(Constants.CARETAKER_ALREADY_PRESENT,userCaretakerException.getMessage());
        }
    }

    @Test
    void saveCaretakerTest() throws UserCaretakerException {
        UserCaretakerDTO userCaretakerDTO= new UserCaretakerDTO();
        userCaretakerDTO.setPatientName("vinay");
        UserCaretaker userCaretaker= new UserCaretaker();
        when(mapper.map(userCaretakerDTO,UserCaretaker.class)).thenReturn(userCaretaker);
        when(userCaretakerRepository.check(userCaretaker.getPatientId(),userCaretaker.getCaretakerId())).thenReturn(null);
        CaretakerResponse caretakerResponseReal= careTakerServiceImpl.saveCareTaker(userCaretakerDTO);
        Assertions.assertEquals(userCaretaker.getPatientName(),caretakerResponseReal.getUserCaretaker().getPatientName());
        Assertions.assertEquals(userCaretaker.getCaretakerId(),caretakerResponseReal.getUserCaretaker().getCaretakerId());
        Assertions.assertEquals(userCaretaker.getPatientId(),caretakerResponseReal.getUserCaretaker().getPatientId());
    }

    @Test
    void updateCaretakerStatusException(){
        UserCaretaker userCaretaker= new UserCaretaker();
        try{
            careTakerServiceImpl.updateCaretakerStatus(userCaretaker.getCaretakerId());
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Constants.NO_RECORD_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void updateCaretakerStatusTest() throws UserCaretakerException {
        UserCaretaker userCaretaker= new UserCaretaker("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay",true,"ftdutsaduifioadf","yfuydfafakjfdafdou","nikunj",LocalDateTime.now(),"p",false);
        when(userCaretakerRepository.findById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(Optional.of(userCaretaker));
        when(userCaretakerRepository.save(userCaretaker)).thenReturn(userCaretaker);
        CaretakerResponse caretakerResponseReal= careTakerServiceImpl.updateCaretakerStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretaker.getCaretakerId(),caretakerResponseReal.getUserCaretaker().getCaretakerId());
        Assertions.assertEquals(userCaretaker.getPatientId(),caretakerResponseReal.getUserCaretaker().getPatientId());
        Assertions.assertEquals(userCaretaker.getCaretakerId(),caretakerResponseReal.getUserCaretaker().getCaretakerId());
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
        }catch (ResourceNotFoundException | UserCaretakerException e){
            Assertions.assertEquals(Constants.PATIENTS_NOT_FOUND,e.getMessage());
        }
    }


    @Test
    void getPatientsUnderMeTest() throws UserCaretakerException, ResourceNotFoundException {
        int pageNo=0;
        int pageSize = 2;
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        UserCaretaker userCaretaker= new UserCaretaker();
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        Page<UserCaretaker> userCaretakerPage= new PageImpl<>(userCaretakerList);
        CaretakerResponsePage caretakerResponsePage= new CaretakerResponsePage(Constants.SUCCESS, Constants.DATA_FOUND,5L,2,0,userCaretakerList);
        when(userCaretakerRepository.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageable)).thenReturn(userCaretakerPage);
        CaretakerResponsePage caretakerResponsePageReal= careTakerServiceImpl.getPatientsUnderMe("73578dfd-e7c9-4381-a348-113e72d80fa2",pageNo,pageSize);
        Assertions.assertEquals(caretakerResponsePage.getUserCaretaker().size(),caretakerResponsePageReal.getUserCaretaker().size());
        Assertions.assertEquals(caretakerResponsePage.getUserCaretaker().get(0).getCaretakerUsername(),caretakerResponsePageReal.getUserCaretaker().get(0).getCaretakerUsername());
        Assertions.assertEquals(caretakerResponsePage.getUserCaretaker().get(0).getCaretakerId(),caretakerResponsePageReal.getUserCaretaker().get(0).getCaretakerId());
    }

    @Test
    void getPatientRequestException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException | ResourceNotFoundException userCaretakerException){
            Assertions.assertEquals(Constants.PATIENT_REQUEST_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getPatientRequestTest() throws UserCaretakerException, ResourceNotFoundException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        CaretakerListResponse caretakerListReal = careTakerServiceImpl.getPatientRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),caretakerListReal.getUserCaretakerList().size());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerId(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerId());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerUsername(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerUsername());
        Assertions.assertEquals(userCaretakerList.get(0).getPatientName(),caretakerListReal.getUserCaretakerList().get(0).getPatientName());
    }

    @Test
    void getMyCaretakersException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException | ResourceNotFoundException userCaretakerException){
            Assertions.assertEquals(Constants.CARETAKERS_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getMyCaretakersTest() throws UserCaretakerException, ResourceNotFoundException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        CaretakerListResponse caretakerListReal = careTakerServiceImpl.getMyCaretakers("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),caretakerListReal.getUserCaretakerList().size());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerId(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerId());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerUsername(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerUsername());
        Assertions.assertEquals(userCaretakerList.get(0).getPatientName(),caretakerListReal.getUserCaretakerList().get(0).getPatientName());
    }

    @Test
    void getCaretakerRequestStatusException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Constants.REQUEST_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getCaretakerRequestStatusTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        List<UserCaretaker> caretakerListReal = careTakerServiceImpl.getCaretakerRequestStatus("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),caretakerListReal.size());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerId(),caretakerListReal.get(0).getCaretakerId());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerUsername(),caretakerListReal.get(0).getCaretakerUsername());
        Assertions.assertEquals(userCaretakerList.get(0).getPatientName(),caretakerListReal.get(0).getPatientName());
    }

    @Test
    void getCaretakerRequestPException(){
        List<UserCaretaker> userCaretakerList= new ArrayList<>();
        when(userCaretakerRepository.getCaretakerRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        try{
            careTakerServiceImpl.getCaretakerRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException userCaretakerException){
            Assertions.assertEquals(Constants.REQUEST_NOT_FOUND,userCaretakerException.getMessage());
        }
    }

    @Test
    void getCaretakerRequestPTest() throws UserCaretakerException {
        UserCaretaker userCaretaker = new UserCaretaker();
        List<UserCaretaker> userCaretakerList = new ArrayList<>();
        userCaretakerList.add(userCaretaker);
        when(userCaretakerRepository.getCaretakerRequests("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerList);
        CaretakerListResponse caretakerListReal = careTakerServiceImpl.getCaretakerRequests("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userCaretakerList.size(),caretakerListReal.getUserCaretakerList().size());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerId(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerId());
        Assertions.assertEquals(userCaretakerList.get(0).getCaretakerUsername(),caretakerListReal.getUserCaretakerList().get(0).getCaretakerUsername());
        Assertions.assertEquals(userCaretakerList.get(0).getPatientName(),caretakerListReal.getUserCaretakerList().get(0).getPatientName());
    }

    @Test
    void delPatientReqException() {
        Optional<UserCaretaker> userCaretaker= Optional.empty();
        when(userCaretakerRepository.findById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretaker);
        try{careTakerServiceImpl.deletePatientRequest("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserCaretakerException e){
            Assertions.assertEquals(Constants.REQUEST_NOT_FOUND,e.getMessage());
        }
    }

    @Test
    void delPatientReqTest() throws UserExceptionMessage, UserCaretakerException {
        UserCaretaker userCaretaker= new UserCaretaker("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay",true,"ftdutsaduifioadf","yfuydfafakjfdafdou","nikunj",LocalDateTime.now(),"p",false);
        Optional<UserCaretaker> userCaretakerTest= Optional.of(userCaretaker);
        when(userCaretakerRepository.findById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userCaretakerTest);
        CaretakerDelete text =careTakerServiceImpl.deletePatientRequest("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(Constants.SUCCESS,text.getStatus());
    }

    @Test
    void sendImageToCaretakerException() {
        MockMultipartFile employeeJson = new MockMultipartFile("employee", null,
                "application/json", "{\"name\": \"Emp Name\"}".getBytes());
        UserMedicines userMedicines= new UserMedicines();
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImageResponse imageResponse= careTakerServiceImpl.sendImageToCaretaker(employeeJson,"fyiaifkvaf","73578dfd-e7c9-4381-a348-113e72d80fa2","PCM",123);
        Assertions.assertEquals(Constants.FAILED,imageResponse.getStatus());


    }

    @Test
    void sendImageToCaretakerTest() throws UserCaretakerException{
        MockMultipartFile employeeJson = new MockMultipartFile("employee", null,
                "application/json", "{\"name\": \"Emp Name\"}".getBytes());
        SendImageDto sendImageDto= new SendImageDto(employeeJson,"Vinay","PCM","73578dfd-e7c9-4381-a348-113e72d80fa2",123);
        User user= new User();
        List<MedicineHistory> medicineHistoryList= Collections.emptyList();
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImageResponse imageResponseTest = careTakerServiceImpl.sendImageToCaretaker(sendImageDto.getImage(),"Something","73578dfd-e7c9-4381-a348-113e72d80fa2","PCM",123);
        Assertions.assertEquals(Constants.SUCCESS,imageResponseTest.getStatus());
    }


}
