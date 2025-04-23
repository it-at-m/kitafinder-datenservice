package de.muenchen.rbs.kitafinderdatenservice.rest;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.muenchen.rbs.kitafinderdatenservice.SecurityConfiguration;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.dto.KindDTO;
import de.muenchen.rbs.kitafinderdatenservice.dto.KindDTOMapper;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(path = "/kind/", produces = "application/json")
@SecurityRequirement(name = "ApiClient", scopes = { SecurityConfiguration.SCOPE_LHM_EXTENDED,
		SecurityConfiguration.SCOPE_OPENID, SecurityConfiguration.SCOPE_ROLES })
public class KindRestController {

	private KindRepository repository;

	private KindDTOMapper mapper = KindDTOMapper.INSTANCE;

	public KindRestController(KindRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	@Operation(summary = "Liefert alle bekannten Kind-Datensätze. Es wird jeweils der aktuellste Datensatz zurückgegeben.")
	public PagedModel<KindDTO> findAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<Kind> result = repository.findAllMostRecent(Pageable.ofSize(size).withPage(page));
		return new PagedModel<>(result.map(k -> mapper.kindToKindDTO(k)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Liefert einen Kind-Datensatz. Es wird der aktuellste Datensatz zur angegebenen Id zurückgegeben.")
	public ResponseEntity<KindDTO> findOne(@PathVariable("id") Integer id) {
		Optional<Kind> result = repository.findMostRecentById(id);

		if (result.isEmpty()) {
			return (ResponseEntity<KindDTO>) ResponseEntity.notFound();
		}

		return ResponseEntity.ok(mapper.kindToKindDTO(result.get()));
	}

}
