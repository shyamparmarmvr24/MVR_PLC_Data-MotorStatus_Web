package com.mvrtechnology.plcdata.security;
import com.mvrtechnology.plcdata.entity.ScadaAccessControl;
import com.mvrtechnology.plcdata.entity.ScadaAccessPlant;
import com.mvrtechnology.plcdata.enums.ScadaAccessType;
import com.mvrtechnology.plcdata.repository.IScadaAccessControlRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScadaAuthorizationService
{
    private final IScadaAccessControlRepo accessRepo;

    @Transactional(readOnly = true)
    public ScadaAccessContext getAccess(String clientIp)
    {
        if (clientIp == null ||
                clientIp.isBlank())
        {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Client IP could not be determined"
            );
        }

        //clientIp = clientIp.trim();


        List<ScadaAccessControl> accesses =
                accessRepo.findByIsActiveTrue();


        ScadaAccessControl exactMatch =
                accesses.stream()
                        .filter(access ->
                                access.getIpAddress() != null &&
                                        access.getIpAddress()
                                                .trim()
                                                .equals(clientIp)
                        )
                        .findFirst()
                        .orElse(null);


        if (exactMatch != null)
        {
            return buildAccessContext(
                    exactMatch
            );
        }

        ScadaAccessControl prefixMatch =
                accesses.stream()

                        .filter(access ->
                                access.getIpAddress() != null
                        )

                        .filter(access ->
                                isPrefixMatch(
                                        clientIp,
                                        access.getIpAddress()
                                )
                        )

                        .max(
                                Comparator.comparingInt(
                                        access ->
                                                access.getIpAddress()
                                                        .trim()
                                                        .length()
                                )
                        )

                        .orElse(null);


        if (prefixMatch != null)
        {
            return buildAccessContext(
                    prefixMatch
            );
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "SCADA access is not configured for this IP"
        );
    }

    private ScadaAccessContext buildAccessContext(ScadaAccessControl access)
    {
        Set<Integer> plantIds = new HashSet<>();

        if (access.getAccessType()
                == ScadaAccessType.PLANT)
        {
            if (access.getPlants() != null)
            {
                for (ScadaAccessPlant accessPlant :
                        access.getPlants())
                {
                    if (accessPlant != null &&
                            accessPlant.getPlant() != null)
                    {
                        plantIds.add(
                                accessPlant
                                        .getPlant()
                                        .getPlantId()
                        );
                    }
                }
            }
        }


        return new ScadaAccessContext(
                access.getAccessId(),
                access.getIpAddress(),
                access.getAccessType(),
                plantIds
        );
    }

    private boolean isPrefixMatch(String clientIp, String configuredIp)
    {
        if (clientIp == null ||
                configuredIp == null)
        {
            return false;
        }


        clientIp =
                clientIp.trim();

        configuredIp = configuredIp.trim();

        int configuredParts =
                configuredIp.split("\\.", -1).length;


        if (configuredParts >= 4)
        {
            return false;
        }

        return clientIp.startsWith(
                configuredIp + "."
        );
    }


    public boolean isAllAccess(
            String clientIp)
    {
        return getAccess(clientIp)
                .hasAllAccess();
    }


    public Set<Integer> getAllowedPlantIds(
            String clientIp)
    {
        ScadaAccessContext access =
                getAccess(clientIp);


        if (access.hasAllAccess())
        {
            return Set.of();
        }


        if (access.plantIds() == null ||
                access.plantIds().isEmpty())
        {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No plants are assigned to this IP"
            );
        }


        return access.plantIds();
    }


    public boolean canAccessPlant(
            String clientIp,
            Integer requestedPlantId)
    {
        return getAccess(clientIp)
                .canAccessPlant(
                        requestedPlantId
                );
    }
}