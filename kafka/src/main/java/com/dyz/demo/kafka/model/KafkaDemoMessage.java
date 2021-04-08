package com.dyz.demo.kafka.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaDemoMessage implements Serializable {

    private String id;

    private String title;

    private String content;
}
