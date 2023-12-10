package albaradimassuntoro.restful.model;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchContactRequest {

  private String name;

  private String email;

  private String phone;

  @NotNull
  private int page;

  @NotNull
  private int size;
}
