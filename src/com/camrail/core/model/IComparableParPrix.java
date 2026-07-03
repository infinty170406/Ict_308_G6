package com.camrail.core.model;

/**
 * Interface pour les objets comparables par leur prix.
 * Utile pour les algorithmes de tri et de recherche.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public interface IComparableParPrix {

    /**
     * Compare deux objets par leur prix TTC
     * 
     * @param autre L'autre objet à comparer
     * @return < 0 si this est moins cher,
     *         0 si égal,
     *         > 0 si this est plus cher
     */
    int comparerParPrix(Object autre);
}
