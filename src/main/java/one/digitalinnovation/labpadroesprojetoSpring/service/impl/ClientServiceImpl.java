package one.digitalinnovation.labpadroesprojetoSpring.service.impl;

import one.digitalinnovation.labpadroesprojetoSpring.model.Adress;
import one.digitalinnovation.labpadroesprojetoSpring.model.AdressRepository;
import one.digitalinnovation.labpadroesprojetoSpring.model.Client;
import one.digitalinnovation.labpadroesprojetoSpring.model.ClientRepository;
import one.digitalinnovation.labpadroesprojetoSpring.service.ClientService;
import one.digitalinnovation.labpadroesprojetoSpring.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AdressRepository adressRepository;
    @Autowired
    private ViaCepService viaCepService;

    // Strategy: Implementar os métodos definidos na interface.
    // Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

    @Override
    public Iterable<Client> findAll() {
        // Buscar todos os Clientes.
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Long id) {
        // Buscar Cliente por ID.
        Optional<Client> client = clientRepository.findById(id);
        return client.get();
    }

    @Override
    public void insert(Client client) {
        salvarClienteComCep(client);
    }

    @Override
    public void update(Long id, Client client) {
        // Buscar Cliente por ID, caso exista:
        Optional<Client> clientBd = clientRepository.findById(id);
        if (clientBd.isPresent()) {
            salvarClienteComCep(client);
        }
    }

    @Override
    public void delete(Long id) {
        // Deletar Cliente por ID.
        clientRepository.deleteById(id);
    }

    private void salvarClienteComCep(Client cliente) {
        // Verificar se o Endereco do Cliente já existe (pelo CEP).
        String cep = cliente.getAdress().getCep();
        Adress endereco = adressRepository.findById(cep).orElseGet(() -> {
            // Caso não exista, integrar com o ViaCEP e persistir o retorno.
            Adress novoEndereco = viaCepService.consultarCep(cep);
            adressRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setAdress(endereco);
        // Inserir Cliente, vinculando o Endereco (novo ou existente).
        clientRepository.save(cliente);
    }
}
