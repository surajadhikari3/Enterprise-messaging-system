package io.reactivestax.EMSRestApi.service.ems;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.ems.Contact;
import io.reactivestax.EMSRestApi.domain.ems.Otp;
import io.reactivestax.EMSRestApi.dto.ems.ClientDTO;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ems.ContactRepository;
import io.reactivestax.EMSRestApi.repository.ems.OTPRepository;
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
                .clientId(client.getClientId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .address(client.getAddress())
                .dateOfBirth(client.getDateOfBirth())
                .isLocked(client.getIsLocked())
                .otpId(client.getOtp().getOtpId())
                .contactId(client.getContact().stream().map(Contact::getContactId).collect(Collectors.toList()))
                .build();

    }

    private Client converToClient(ClientDTO clientDTO) {

        return Client.builder()
                .clientId(clientDTO.getClientId())
                .firstName(clientDTO.getFirstName())
                .lastName(clientDTO.getLastName())
                .address(clientDTO.getAddress())
                .dateOfBirth(clientDTO.getDateOfBirth())
                .contact(fetchContactById(clientDTO.getContactId()))
                .otp(fetchOtpById(clientDTO.getOtpId()))
                .isLocked(clientDTO.getIsLocked())
                .build();

    }


    private List<Contact> fetchContactById(List<Long> contactIds) {
        return contactRepository.findAllById(contactIds);
    }

    private Otp fetchOtpById(Long otpId) {
        return otpRepository.findById(otpId)
                .orElseThrow(() -> new RuntimeException("OTP not found"));
    }

}
