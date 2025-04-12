package com.uts.chatbotuts.infrastructure.adapter.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileFaqDTO {
    private Long faqId;
    private String fileName;

}
