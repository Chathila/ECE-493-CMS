package com.ece493.cms.service;

import com.ece493.cms.model.InvitationResponse;

import java.util.List;
import java.util.Optional;

public interface InvitationResponseRepository {
    InvitationResponse save(InvitationResponse response);

    Optional<InvitationResponse> findByInvitationId(long invitationId);

    List<InvitationResponse> findAll();
}
