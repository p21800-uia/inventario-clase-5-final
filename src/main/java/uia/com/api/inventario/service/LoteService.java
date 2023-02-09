package uia.com.api.inventario.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uia.com.api.inventario.dto.LotesDTO;
import uia.com.api.inventario.mapper.ApiMapperLote;
import uia.com.api.inventario.model.Embalaje;
import uia.com.api.inventario.model.Lote;
import uia.com.api.inventario.repository.EmbalajeRepository;
import uia.com.api.inventario.repository.LoteRepository;

import java.util.List;
import java.util.Set;


@Service
public class LoteService {
    LoteRepository repositoryLotes;
    EmbalajeRepository repositoryEmbalaje;

    Validator validator;

    @Autowired
    public LoteService(LoteRepository repositoryLotes, EmbalajeRepository repositoryEmbalaje, Validator validator) {
        this.repositoryLotes = repositoryLotes;
        this.repositoryEmbalaje = repositoryEmbalaje;
        this.validator = validator;
    }
    public LotesDTO save(LotesDTO lote)
    {
        return saveInformation(lote);
    }

    public LotesDTO update(LotesDTO lote) {
        return saveInformation(lote);
    }

    private LotesDTO saveInformation(LotesDTO lotesDTO)
    {
        LotesDTO lotes_salvados = new LotesDTO();

        if(lotesDTO.getLotes().size() > 0)
        {
            for(int i=0; i<lotesDTO.getLotes().size(); i++)
            {
                String nameEmbalaje = lotesDTO.getLotes().get(i).getNameEmbalaje();
                String id = nameEmbalaje;
                if(!this.repositoryEmbalaje.existsById(id))
                {
                    List<Embalaje> embalajes = this.repositoryEmbalaje.findByName(nameEmbalaje);

                    Lote entity = ApiMapperLote.INSTANCE.DTOToEntity(lotesDTO.getLotes().get(i));
                    //entity.setId(entity.getName());
                    entity.setEmbalaje(embalajes.get(0));
                    embalajes.get(0).getLotes().add(entity);  //--usado para la relacion de regreso
                    this.repositoryEmbalaje.save(embalajes.get(0));   //--usado para la relacion de regreso

                    Set<ConstraintViolation<Lote>> violations = validator.validate(entity);
                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }
                    Lote savedEntity = repositoryLotes.save(entity);
                    lotes_salvados.getLotes().add(ApiMapperLote.INSTANCE.entityToDTO(savedEntity));
                }
            }
        }
        return lotes_salvados;
    }

}
