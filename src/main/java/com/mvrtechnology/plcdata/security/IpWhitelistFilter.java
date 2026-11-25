package com.mvrtechnology.plcdata.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvrtechnology.plcdata.enums.ScadaAccessType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class IpWhitelistFilter
        extends OncePerRequestFilter
{
    private static final String API_PREFIX = "/api/";


    private final ClientIpResolver clientIpResolver;

    private final ScadaAuthorizationService authorizationService;

    private final ScadaEndpointPolicy endpointPolicy;

    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        String uri = request.getRequestURI();

        if (!uri.startsWith(API_PREFIX))
        {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        if ("OPTIONS".equalsIgnoreCase(
                request.getMethod()))
        {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String clientIp = clientIpResolver.resolve(request);

        ScadaAccessContext access;

        try
        {
            access =
                    authorizationService.getAccess(
                            clientIp
                    );
        }
        catch(Exception ex)
        {
            deny(
                    response,
                    "ACCESS_DENIED",
                    "This IP is not authorized to access SCADA"
            );

            return;
        }

        request.setAttribute(
                "SCADA_CLIENT_IP",
                clientIp
        );

        request.setAttribute(
                "SCADA_ACCESS_TYPE",
                access.accessType()
        );

        request.setAttribute(
                "SCADA_PLANT_IDS",
                access.plantIds()
        );

        if (access.accessType()
                == ScadaAccessType.ALL)
        {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        if (access.accessType()
                != ScadaAccessType.PLANT)
        {
            deny(
                    response,
                    "INVALID_ACCESS_TYPE",
                    "Invalid SCADA access configuration"
            );

            return;
        }

        Set<Integer> allowedPlantIds =
                access.plantIds();


        if (allowedPlantIds == null ||
                allowedPlantIds.isEmpty())
        {
            deny(
                    response,
                    "PLANT_NOT_CONFIGURED",
                    "No valid plants are assigned to this IP"
            );

            return;
        }

        if (endpointPolicy.isAllPlantEndpoint(
                request))
        {
            deny(
                    response,
                    "ALL_PLANT_ACCESS_DENIED",
                    "This IP is authorized only for Plants "
                            + allowedPlantIds
            );

            return;
        }

        if (endpointPolicy.isGlobalEndpoint(
                request))
        {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        if (isPlantListEndpoint(request))
        {
            request.setAttribute(
                    "SCADA_ALLOWED_PLANT_IDS",
                    allowedPlantIds
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        Integer requestedPlantId = extractPlantId(request);

        if (requestedPlantId == null)
        {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        if (requestedPlantId <= 0)
        {
            deny(
                    response,
                    "INVALID_PLANT_ID",
                    "Invalid plant ID"
            );

            return;
        }

        if (!allowedPlantIds.contains(
                requestedPlantId))
        {
            deny(
                    response,
                    "PLANT_ACCESS_DENIED",
                    "You are not authorized to access Plant "
                            + requestedPlantId
            );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }


    private Integer extractPlantId(
            HttpServletRequest request)
    {
        Integer queryPlantId =
                parsePlantId(
                        request.getParameter(
                                "plantId"
                        )
                );


        if (queryPlantId != null)
        {
            return queryPlantId;
        }

        String[] parameterNames =
                {
                        "plantID",
                        "plant_id",
                        "id"
                };


        for (String parameterName :
                parameterNames)
        {
            Integer value =
                    parsePlantId(
                            request.getParameter(
                                    parameterName
                            )
                    );


            if (value != null)
            {
                return value;
            }
        }


        /*
         * Path variable.
         */
        return extractPlantIdFromPath(
                request.getRequestURI()
        );
    }


    private Integer parsePlantId(
            String value)
    {
        if (value == null ||
                value.isBlank())
        {
            return null;
        }


        try
        {
            return Integer.valueOf(
                    value.trim()
            );
        }
        catch(NumberFormatException ex)
        {
            return -1;
        }
    }


    private Integer extractPlantIdFromPath(
            String uri)
    {
        String[] prefixes =
                {
                        "/api/plants/",
                        "/api/solar/",
                        "/api/solar/sse/",
                        "/api/effluent/data/",
                        "/api/effluent/live/",
                        "/api/machinery-status/by-plant/"
                };


        for (String prefix :
                prefixes)
        {
            if (uri.startsWith(prefix))
            {
                String remaining =
                        uri.substring(
                                prefix.length()
                        );


                if (remaining.endsWith("/"))
                {
                    remaining =
                            remaining.substring(
                                    0,
                                    remaining.length() - 1
                            );
                }


                String firstPart =
                        remaining.split("/")[0];


                if (firstPart.matches("\\d+"))
                {
                    return Integer.valueOf(
                            firstPart
                    );
                }


                if (!firstPart.isBlank())
                {
                    return -1;
                }
            }
        }


        return null;
    }


    private boolean isPlantListEndpoint(
            HttpServletRequest request)
    {
        return request.getRequestURI()
                .equals("/api/plants")
                &&
                "GET".equalsIgnoreCase(
                        request.getMethod()
                );
    }


    private void deny(
            HttpServletResponse response,
            String error,
            String message)
            throws IOException
    {
        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );


        objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                        "error",
                        error,
                        "message",
                        message
                )
        );
    }
}