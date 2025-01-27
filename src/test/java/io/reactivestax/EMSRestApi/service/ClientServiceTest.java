package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Contact;
import io.reactivestax.EMSRestApi.dto.ClientDTO;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ContactRepository;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @MockitoBean
    private ClientRepository clientRepository;

    @MockitoBean
    private ContactRepository contactRepository;

    @MockitoBean
    private OTPRepository otpRepository;


    @Test
    void testFindAll() {
        Client client1 = Client.builder()
                .id(1L)
                .firstName("Suraj")
                .lastName("Dev")
                .address("123 Main St")
                .contacts(Arrays.asList(Contact.builder().id(101L).build()))
                .build();

        Client client2 = Client.builder()
                .id(2L)
                .firstName("Pema")
                .lastName("Dev")
                .address("456 Elm St")
                .contacts(Arrays.asList(Contact.builder().id(102L).build()))
                .build();

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2));

        List<ClientDTO> result = clientService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Suraj");
        assertThat(result.get(1).getFirstName()).isEqualTo("Pema");
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        ClientDTO clientDTO = ClientDTO.builder()
                .clientId(1L)
                .firstName("Suraj")
                .lastName("Dev")
                .address("45 Elm St")
                .contactId(Arrays.asList(101L))
                .build();

        Contact contact = Contact.builder().id(101L).build();

        Client client = Client.builder()
                .id(1L)
                .firstName("Suraj")
                .lastName("Dev")
                .address("45 Elm St")
                .contacts(Arrays.asList(contact))
                .build();

        when(contactRepository.findAllById(clientDTO.getContactId())).thenReturn(Arrays.asList(contact));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.save(clientDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Suraj");
        assertThat(result.getContactId()).containsExactly(101L);
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(contactRepository, times(1)).findAllById(clientDTO.getContactId());
    }

    @Test
    void testFindById_Success() {
        Client client = Client.builder()
                .id(1L)
                .firstName("Suraj")
                .lastName("Dev")
                .address("45 Elm St")
                .contacts(Arrays.asList(Contact.builder().id(101L).build()))
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Optional<ClientDTO> result = clientService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Suraj");
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<ClientDTO> result = clientService.findById(1L);

        // Assert
        assertThat(result).isNotPresent();
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        Long clientId = 1L;

        doNothing().when(clientRepository).deleteById(clientId);

        clientService.deleteById(clientId);

        verify(clientRepository, times(1)).deleteById(clientId);
    }
}