package com.ecoride.trust.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TrustConnectionId implements Serializable {
    private UUID user1Id;
    private UUID user2Id;
}
