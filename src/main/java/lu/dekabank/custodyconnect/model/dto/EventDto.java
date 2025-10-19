package lu.dekabank.custodyconnect.model.dto;

import jakarta.validation.constraints.NotBlank;

public class EventDto {
  @NotBlank public String type;
  @NotBlank public String sourceSystem;
  // raw JSON/XML as string (optional)
  public String payload;
}
