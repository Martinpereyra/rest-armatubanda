package com.restapi.armatubanda.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicianInvitationStatusDto {
    public MusicianBandsDto musicianBandsDto;
    public String status;
    public MusicianInvitationStatusDto(MusicianBandsDto musicianBandsDto, String status) {
        this.musicianBandsDto = musicianBandsDto;
        this.status = status;
    }

}
