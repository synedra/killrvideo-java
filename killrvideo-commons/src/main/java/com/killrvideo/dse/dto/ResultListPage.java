package com.killrvideo.dse.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;

/**
 * Ease usage of the paginState.
 *
 * @author DataStax Developer Advocates team.
 */
public class ResultListPage < ENTITY > {

	/** Results map as entities. */
	private List<ENTITY> listOfResults = new ArrayList<>();
	
	/** Custom management of paging state. */
	private Optional< String > nextPage = Optional.empty();

	/**
	 * Default Constructor.
	 */
	public ResultListPage() {}
	
	/**
     * Constructor from a RESULT.
     * 
     * @param rs
     *      result set
     * @param mapper
     *      mapper
     */
    public ResultListPage(PagingIterable<ENTITY> rs) {
        if (null != rs) {
            Iterator<ENTITY> iterResults = rs.iterator();
            // rs.getAvailableWithoutFetching() all to parse only current page without fecthing all
            IntStream.range(0, rs.getAvailableWithoutFetching())
                     .forEach(item -> listOfResults.add(iterResults.next()));
            if (null != rs.getExecutionInfo().getPagingState()) {
                nextPage = Optional.ofNullable(rs.getExecutionInfo().getPagingState().toString());
            }
        }
    }
    
    public ResultListPage(MappedAsyncPagingIterable<ENTITY> rs) {
        if (null != rs) {
           rs.currentPage().forEach(listOfResults::add);
           nextPage = Optional.ofNullable(rs.getExecutionInfo().getPagingState().toString());
        }
    }
    
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (null != listOfResults) {
			sb.append("Results:");
			sb.append(listOfResults.toString());
		}
		if (nextPage.isPresent()) {
			sb.append("\n + pagingState is present : ");
			sb.append(nextPage.get());
		}
		return sb.toString();
	}
	
	/**
	 * Getter for attribute 'listOfResults'.
	 *
	 * @return current value of 'comments'
	 */
	public List<ENTITY> getResults() {
		return listOfResults;
	}

	/**
	 * Setter for attribute 'listOfResults'.
	 * 
	 * @param comments
	 *            new value for 'comments '
	 */
	public void setresults(List<ENTITY> comments) {
		this.listOfResults = comments;
	}

	/**
	 * Getter for attribute 'listOfResults'.
	 *
	 * @return current value of 'pagingState'
	 */
	public Optional<String> getPagingState() {
		return nextPage;
	}

	/**
	 * Setter for attribute 'pagingState'.
	 * 
	 * @param pagingState
	 *            new value for 'pagingState '
	 */
	public void setPagingState(Optional<String> pagingState) {
		this.nextPage = pagingState;
	}

}
