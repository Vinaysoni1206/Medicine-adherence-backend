package com.example.user_service.service.medicine;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.Image;
import com.example.user_service.model.MedicineHistory;
import com.example.user_service.model.UserMedicines;
import com.example.user_service.model.User;
import com.example.user_service.pojos.request.MedicineDTO;
import com.example.user_service.pojos.request.MedicineHistoryDTO;
import com.example.user_service.pojos.response.MedicineResponse;
import com.example.user_service.repository.ImageRepository;
import com.example.user_service.repository.UserMedHistoryRepository;
import com.example.user_service.repository.UserMedicineRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.impl.UserMedicineServiceImpl;
import com.example.user_service.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.user_service.util.Constants.MEDICINES_NOT_FOUND;
import static com.example.user_service.util.Constants.USER_NOT_FOUND;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserMedicineServiceTest {

    UserMedicineServiceImpl userMedicineServiceImpl;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMedicineRepository userMedicineRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    UserMedHistoryRepository userMedHistoryRepository;

    @BeforeEach
    public void init(){
        userMedicineServiceImpl= new UserMedicineServiceImpl(userRepository,userMedicineRepository,imageRepository,userMedHistoryRepository);
    }

    @Test
    void getAllUserMedicinesExceptionTest(){
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(null);
        try{
            userMedicineServiceImpl.getAllUserMedicines("73578dfd-e7c9-4381-a348-113e72d80fa2");
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals(USER_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }

    @Test
    void getAllUserMedicinesTest() throws UserExceptionMessage, ExecutionException, InterruptedException {
        UserMedicines userMedicines= new UserMedicines();
        List<UserMedicines> userMedicines1= new ArrayList<>();
        userMedicines1.add(userMedicines);
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,userMedicines1);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        CompletableFuture<List<UserMedicines>> listCompletableFuture= userMedicineServiceImpl.getAllUserMedicines("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(1,listCompletableFuture.get().size());
    }

    @Test
    void synDataException() {
        List<MedicineDTO> medicinePojoList = new ArrayList<>();
        List<UserMedicines> userMedicines1= new ArrayList<>();
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,userMedicines1);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        try{
            userMedicineServiceImpl.syncData("73578dfd-e7c9-4381-a348-113e72d80fa2",medicinePojoList);
        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals("Unable to sync",userMedicineException.getMessage());
        }
    }

    @Test
    void syncDataTest() throws UserMedicineException {
        MedicineDTO medicinePojo= new MedicineDTO(123,"Mon",1,null,"something",10,"PCM","something",null,0,"10:00 AM");
        List<MedicineDTO> medicinePojoList = new ArrayList<>();
        medicinePojoList.add(medicinePojo);
        List<MedicineHistory> medicineHistoryList = new ArrayList<>();
        List<UserMedicines> userMedicinesList= new ArrayList<>();
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,userMedicinesList);
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        userMedicinesList.add(userMedicines);
        MedicineHistory medicineHistory= new MedicineHistory(123,null,"10:00 AM",null,null);
        medicineHistoryList.add(medicineHistory);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        String value =userMedicineServiceImpl.syncData("73578dfd-e7c9-4381-a348-113e72d80fa2",medicinePojoList);
        Assertions.assertEquals(Constants.SUCCESS,value);
    }

    @Test
    void syncMedicineHistoryException(){
        when(userMedicineRepository.getMedById(123)).thenReturn(null);
        try{
            userMedicineServiceImpl.syncMedicineHistory(123,null);
        }catch (UserMedicineException e){
            Assertions.assertEquals("Unable to sync",e.getMessage());
        }
    }

    @Test
    void syncMedicineHistoryTest() throws UserMedicineException {
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        MedicineHistory medicineHistory= new MedicineHistory(123,null,"10:00 AM",null,null);
        List<MedicineHistory> medicineHistoryList = new ArrayList<>();
        medicineHistoryList.add(medicineHistory);
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        String[] timing= new String[4];
        timing[0]="10:00 AM";
        timing[1]="12:00 PM";
        MedicineHistoryDTO medicineHistoryDTO= new MedicineHistoryDTO(1234,"12 June", timing,timing);
        MedicineHistoryDTO medicineHistoryDTO1= new MedicineHistoryDTO(1235,"12 June",timing,timing);
        MedicineHistoryDTO medicineHistoryDTO2= new MedicineHistoryDTO(1236,"12 June",timing,timing);
        MedicineHistoryDTO medicineHistoryDTO3= new MedicineHistoryDTO(1237,"12 June",timing,timing);
        List<MedicineHistoryDTO> medicineHistoryDTOList= new ArrayList<>();
        medicineHistoryDTOList.add(medicineHistoryDTO);
        medicineHistoryDTOList.add(medicineHistoryDTO1);
        medicineHistoryDTOList.add(medicineHistoryDTO2);
        medicineHistoryDTOList.add(medicineHistoryDTO3);
        MedicineResponse medicineResponse= userMedicineServiceImpl.syncMedicineHistory(123,medicineHistoryDTOList);
        Assertions.assertEquals(medicineHistoryDTOList.size(),medicineResponse.getUserMedicinesList().size());
    }

    @Test
    void getMedicineHistoryException(){
        User user= new User();
        List<MedicineHistory> medicineHistoryList= Collections.emptyList();
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        try{
            userMedicineServiceImpl.getMedicineHistory(123);
        }catch (UserMedicineException e){
            Assertions.assertEquals(MEDICINES_NOT_FOUND,e.getMessage());
        }
    }

    @Test
    void getMedicineHistoryTest() throws UserMedicineException {
        User user = new User("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),null,null);
        MedicineHistory medicineHistory= new MedicineHistory(123,null,"10:00 AM",null,null);
        List<MedicineHistory> medicineHistoryList = new ArrayList<>();
        medicineHistoryList.add(medicineHistory);
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,null);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        MedicineResponse medicineResponse= userMedicineServiceImpl.getMedicineHistory(123);
        Assertions.assertEquals(medicineHistoryList.size(),medicineResponse.getUserMedicinesList().size());
        Assertions.assertEquals(medicineHistoryList.get(0).getHistoryId(),medicineResponse.getUserMedicinesList().get(0).getHistoryId());
    }

    @Test
    void getUserMedicneImagesTest(){
        User user= new User();
        Image image= new Image();
        List<Image> imageList= new ArrayList<>();
        imageList.add(image);
        List<MedicineHistory> medicineHistoryList= Collections.emptyList();
        UserMedicines userMedicines= new UserMedicines(123,"asjdjajd","PCM","something","Mon","fafafafa","10:00 AM","anything",12,5,user,medicineHistoryList,imageList);
        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        List<Image> imageList1= userMedicineServiceImpl.getUserMedicineImages(123);
        Assertions.assertEquals(imageList.size(),imageList1.size());
        Assertions.assertEquals(imageList.get(0).getImageId(),imageList1.get(0).getImageId());
    }
}