package DTO;

import java.util.LinkedList;
import java.util.List;

public class LoanForSaleDTOList {
    List<LoanForSaleDTO> loansForSaleDTO = new LinkedList<>();

    public LoanForSaleDTOList(List<LoanForSaleDTO> loansForSaleDTO) {
        this.loansForSaleDTO = loansForSaleDTO;
    }

    public List<LoanForSaleDTO> getLoansForSaleDTO() {
        return loansForSaleDTO;
    }

    public void add(LoanForSaleDTO loanForSaleDTO){
        loansForSaleDTO.add(loanForSaleDTO);
    }
    public void clear(){
        this.loansForSaleDTO.clear();
    }
}
