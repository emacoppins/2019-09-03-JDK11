package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	List<Portion>best;
	FoodDao dao;
	Map<String, Portion>idMap;
	SimpleWeightedGraph<Portion, DefaultWeightedEdge>grafo;
	int peso;
	

	public Model() {
		dao= new FoodDao();
		idMap=new HashMap<>();
		dao.AllPortions(idMap);
		
	}

	public void creaGrafo(int calories) {
	
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.listVertex(calories, idMap));
		for(Adiacenza a: dao.listEdges(idMap)) {
			if(grafo.containsVertex(a.getP1())) {
				if(grafo.containsVertex(a.getP2())) {
					Graphs.addEdge(grafo, a.getP1(), a.getP2(), a.getPeso());
				}
			}
		}
	}


	public List <Portion>typePortions(){
		List<Portion>portions=new ArrayList<>(this.idMap.values());
		Collections.sort(portions);
		
		return portions;
	}
	public int nvertici() {
		return grafo.vertexSet().size();
	}
	
	public int narchi() {
		return grafo.edgeSet().size();
	}
	
	
	
	public List<Portion>trovaCammino(Portion p, int lunghezza){
		best=new ArrayList<>();
		peso=0;
		List<Portion>parziale=new ArrayList<>();
		parziale.add(p);
		ricorsione(parziale,lunghezza,0);
		return best;
	}

	public List<Portion> getBest() {
		return best;
	}

	public int getPeso() {
		return peso;
	}

	private void ricorsione(List<Portion> parziale, int lunghezza, int peso) {
		
		if(parziale.size()==lunghezza) {
			if(this.peso<peso) {
				this.peso=peso;
				best=new ArrayList<>(parziale);
			}
		}
		
		for(Portion temp: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1) )) {
			if(!parziale.contains(temp)) {
				peso+=grafo.getEdgeWeight(grafo.getEdge(parziale.get(parziale.size()-1), temp));
				parziale.add(temp);
				
				ricorsione(parziale,lunghezza,peso);
				parziale.remove(parziale.size()-1);
			}
		}
		
		
		
	}
}
