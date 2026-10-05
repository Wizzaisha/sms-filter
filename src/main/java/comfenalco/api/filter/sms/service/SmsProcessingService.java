package comfenalco.api.filter.sms.service;

import comfenalco.api.filter.sms.dto.*;
import comfenalco.api.filter.sms.repository.AgendaSmsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SmsProcessingService {

    private final PhoneNormalizer phoneNormalizer;

    private final AgendaSmsRepository agendaSmsRepository;

    @Transactional
    public ProcessResponse process(
            ProcessRequest request) {

        validateRequest(request);

        List<ValidRecordResponse> validRecords =
                new ArrayList<ValidRecordResponse>();

        List<DiscardedRecordResponse> discardedRecords =
                new ArrayList<DiscardedRecordResponse>();

        /*
         * Permite detectar duplicados dentro
         * del mismo request.
         *
         * Ejemplo:
         *
         * 3001234567|2026-10-05
         */
        Set<String> recordsInRequest =
                new HashSet<String>();

        int discardedByFormat = 0;

        int discardedByInternalDuplicate = 0;

        int discardedByDatabaseOccupation = 0;

        /*
         * =========================================================
         * PROCESAR REGISTROS
         * =========================================================
         */

        for (SmsRecordRequest record
                : request.getRegistros()) {

            /*
             * -----------------------------------------------------
             * 1. VALIDAR TELÉFONO
             * -----------------------------------------------------
             */

            String telefonoNormalizado =
                    phoneNormalizer.normalize(
                            record.getTelefono()
                    );

            /*
             * -----------------------------------------------------
             * 2. VALIDAR FECHA
             * -----------------------------------------------------
             *
             * Al utilizar LocalDate, Jackson ya valida que:
             *
             * 2026-10-05
             *
             * tenga un formato correcto.
             *
             * Si llega null, también lo consideramos inválido.
             */

            LocalDate fechaEnvio =
                    record.getFechaEnvio();

            if (telefonoNormalizado == null
                    || fechaEnvio == null) {

                discardedRecords.add(
                        DiscardedRecordResponse.builder()
                                .telefonoIngresado(
                                        record.getTelefono()
                                )
                                .telefonoNormalizado(
                                        telefonoNormalizado
                                )
                                .fechaProgramada(
                                        fechaEnvio
                                )
                                .area(
                                        resolveArea(
                                                record,
                                                request
                                        )
                                )
                                .motivoRechazo(
                                        buildValidationReason(
                                                telefonoNormalizado,
                                                fechaEnvio
                                        )
                                )
                                .build()
                );

                discardedByFormat++;

                continue;
            }

            /*
             * -----------------------------------------------------
             * 3. DUPLICADO DENTRO DEL REQUEST
             * -----------------------------------------------------
             */

            String recordKey =
                    telefonoNormalizado
                            + "|"
                            + fechaEnvio;

            if (!recordsInRequest.add(recordKey)) {

                discardedRecords.add(
                        DiscardedRecordResponse.builder()
                                .telefonoIngresado(
                                        record.getTelefono()
                                )
                                .telefonoNormalizado(
                                        telefonoNormalizado
                                )
                                .fechaProgramada(
                                        fechaEnvio
                                )
                                .area(
                                        resolveArea(
                                                record,
                                                request
                                        )
                                )
                                .motivoRechazo(
                                        "Duplicado en el mismo archivo"
                                )
                                .build()
                );

                discardedByInternalDuplicate++;

                continue;
            }

            /*
             * -----------------------------------------------------
             * 4. INTENTAR RESERVAR
             * -----------------------------------------------------
             */

            String area =
                    resolveArea(
                            record,
                            request
                    );

            boolean reserved =
                    agendaSmsRepository.tryReserve(
                            telefonoNormalizado,
                            fechaEnvio,
                            area
                    );

            /*
             * -----------------------------------------------------
             * 5. RESULTADO
             * -----------------------------------------------------
             */

            if (reserved) {

                /*
                 * El número estaba libre.
                 *
                 * Se creó la reserva en agenda_sms.
                 */
                validRecords.add(
                        ValidRecordResponse.builder()
                                .telefonoOriginal(
                                        record.getTelefono()
                                )
                                .telefono(
                                        telefonoNormalizado
                                )
                                .fechaProgramada(
                                        fechaEnvio
                                )
                                .area(area)
                                .estado("Ocupado")
                                .build()
                );

            } else {

                /*
                 * El número ya estaba reservado
                 * para esa fecha.
                 */
                String areaPropietaria =
                        agendaSmsRepository.findOwner(
                                telefonoNormalizado,
                                fechaEnvio
                        );

                discardedRecords.add(
                        DiscardedRecordResponse.builder()
                                .telefonoIngresado(
                                        record.getTelefono()
                                )
                                .telefonoNormalizado(
                                        telefonoNormalizado
                                )
                                .fechaProgramada(
                                        fechaEnvio
                                )
                                .area(area)
                                .motivoRechazo(
                                        "Ocupado previamente por el área: "
                                                + areaPropietaria
                                )
                                .build()
                );

                discardedByDatabaseOccupation++;
            }
        }

        /*
         * =========================================================
         * 6. RESUMEN
         * =========================================================
         */

        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalRecordsExcel(
                                request.getRegistros().size()
                        )
                        .validApproved(
                                validRecords.size()
                        )
                        .totalDiscarded(
                                discardedRecords.size()
                        )
                        .discardedByFormat(
                                discardedByFormat
                        )
                        .discardedByInternalDuplicate(
                                discardedByInternalDuplicate
                        )
                        .discardedByDatabaseOccupation(
                                discardedByDatabaseOccupation
                        )
                        .build();

        return ProcessResponse.builder()
                .summary(summary)
                .validRecords(validRecords)
                .discardedRecords(discardedRecords)
                .build();
    }

    /*
     * =============================================================
     * VALIDACIÓN DEL REQUEST
     * =============================================================
     */

    private void validateRequest(
            ProcessRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "El cuerpo de la solicitud es obligatorio."
            );
        }

        if (request.getRegistros() == null
                || request.getRegistros().isEmpty()) {

            throw new IllegalArgumentException(
                    "La solicitud debe contener al menos un registro."
            );
        }
    }

    /*
     * =============================================================
     * ÁREA
     * =============================================================
     */

    private String resolveArea(
            SmsRecordRequest record,
            ProcessRequest request) {

        /*
         * Utilizamos primero el área del registro,
         * porque forma parte del contrato enviado
         * por el frontend.
         */
        if (record.getArea() != null
                && !record.getArea().trim().isEmpty()) {

            return record.getArea().trim();
        }

        /*
         * Como respaldo utilizamos el área general
         * del request.
         */
        if (request.getArea() != null
                && !request.getArea().trim().isEmpty()) {

            return request.getArea().trim();
        }

        return "General";
    }

    /*
     * =============================================================
     * MOTIVO DE VALIDACIÓN
     * =============================================================
     */

    private String buildValidationReason(
            String telefono,
            LocalDate fecha) {

        if (telefono == null
                && fecha == null) {

            return "Teléfono y fecha inválidos";
        }

        if (telefono == null) {

            return "Formato de teléfono inválido o incompleto";
        }

        return "Fecha de envío inválida";
    }
}
