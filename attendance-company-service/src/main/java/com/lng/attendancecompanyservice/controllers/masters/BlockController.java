package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.BlockService;
import com.lng.dto.masters.block.BlockDto;
import com.lng.dto.masters.block.BlockResponse;



@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/block")
public class BlockController {
	
	@Autowired
	BlockService blockService;
	
	@PostMapping(value="/create")
	public ResponseEntity<BlockResponse>save(@RequestBody BlockDto blockDto){
		BlockResponse blockDto1 = blockService.saveBlock(blockDto);
		if(blockDto!=  null){
			return new ResponseEntity<BlockResponse>(blockDto1,HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<BlockResponse> getAll() {
		BlockResponse blockDto =  blockService.getAll();
		if(blockDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<BlockResponse>(blockDto, HttpStatus.OK);
	}

	@PostMapping(value = "/deleteByBlockId")
	public ResponseEntity<BlockResponse> delete(@RequestBody BlockDto blockDto) {
		BlockResponse blockDto2 = blockService.deleteByBlkId(blockDto.getBlkId());
		if(blockDto!=null){
			return new ResponseEntity<BlockResponse>(blockDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}


	@PostMapping(value="/updateByBlockId")
	public ResponseEntity<status.Status> update(@RequestBody BlockDto blockDto){
		status.Status status = blockService.updateBlockByBlkId(blockDto);
		if(blockDto != null){
			return  new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getBranchDetailsByCustomerId")
	public ResponseEntity<BlockResponse> edit1(@RequestBody BlockDto blockDto){
		BlockResponse blockDto1 = blockService.getBranchDetailsByCustId(blockDto.getCustId());
		if(blockDto !=null){
			return new ResponseEntity<BlockResponse>(blockDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getBranchListByCustomerId")
	public ResponseEntity<BlockResponse> getBranchList(@RequestBody BlockDto blockDto){
		BlockResponse blockDto1 = blockService.getBranchesByCustId(blockDto.getCustId());
		if(blockDto !=null){
			return new ResponseEntity<BlockResponse>(blockDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/getBlockDetailsByCustomerIdAndBranchId")
	public ResponseEntity<BlockResponse> edit (@RequestBody BlockDto blockDto){
		BlockResponse blockDto1 = blockService.getBlockDetailsByCustIdANDRefBranchId(blockDto.getCustId(), blockDto.getRefBranchId());
		if(blockDto !=null){
			return new ResponseEntity<BlockResponse>(blockDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getBlockDetailsByBlockId")
	public ResponseEntity<BlockResponse> findByBlockId(@RequestBody BlockDto blockDto) {
		BlockResponse blockResponse = blockService.getBlockByBlkId(blockDto.getBlkId());
		if (blockResponse !=null){
			return new ResponseEntity<BlockResponse>(blockResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<BlockResponse> edit2(@RequestBody BlockDto blockDto) {
		BlockResponse blockResponse = blockService.getByCustomer_CustId(blockDto.getCustId());
		if(blockDto !=null){
			return new ResponseEntity<BlockResponse>(blockResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}


	
	/*
	 * @PostMapping(value = "/getAllByCustId") public ResponseEntity<BlockResponse>
	 * getByCustId(@RequestBody BlockDto blockDto) { BlockResponse blockResponse =
	 * blockService.getByCustomer_CustId(blockDto.getCustId()); if(blockDto !=null){
	 * return new ResponseEntity<BlockResponse>(blockResponse, HttpStatus.CREATED);
	 * } return new ResponseEntity(HttpStatus.NO_CONTENT); }
	 */

}