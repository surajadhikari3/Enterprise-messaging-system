package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Contact;
import io.reactivestax.EMSRestApi.dto.ClientDTO;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ContactRepository;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private OTPRepository otpRepository;


    public List<ClientDTO> findAll() {
        return clientRepository.findAll().stream().map(this::convertToClientDTO).collect(Collectors.toList());
    }

    public ClientDTO save(ClientDTO clientDTO) {
        Client client = converToClient(clientDTO);
        return convertToClientDTO(clientRepository.save(client));
    }


    public Optional<ClientDTO> findById(Long id) {
        return clientRepository.findById(id).map(this::convertToClientDTO);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }


    private ClientDTO convertToClientDTO(Client client) {
        return ClientDTO
                .builder()
                .clientId(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .address(client.getAddress())
                .dateOfBirth(client.getDateOfBirth())
                .contactId(client.getContacts().stream().map(Contact::getId).collect(Collectors.toList()))
                .build();

    }

    private Client converToClient(ClientDTO clientDTO) {

        return Client.builder()
                .id(clientDTO.getClientId())
                .firstName(clientDTO.getFirstName())
                .lastName(clientDTO.getLastName())
                .address(clientDTO.getAddress())
                .dateOfBirth(clientDTO.getDateOfBirth())
                .contacts(fetchContactById(clientDTO.getContactId()))
                .build();
    }


    private List<Contact> fetchContactById(List<Long> contactIds) {
        return contactRepository.findAllById(contactIds);
    }


}