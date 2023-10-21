package com.restapi.armatubanda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationStatusDto {

    private int invitationId;
    private int musicianId;
    private int bandId;
    private boolean status;
}


