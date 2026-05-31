# refactoring code
    What improvements can you make to the design/architecture of your code?
    TBA

    How can you split up large methods or classes into smaller components?
    TBA

    Are there unused files/methods that can be removed?
     -All unused files have been removed.

    Where would additional Java interfaces be appropriate?
    TBA

    How can you make things simpler, more-usable, and easier to maintain?
    - we can do this by adjusting the larger files moving heavier logic into another file.
    - for testing output we could adjust the command to track the centroid in a dedicated file rather than multiple ones.

    Other refactoring improvements?
    - Adjusting the csv file out put because the naming for the file is a bit weird.

# adding tests
    What portions of your code are untested / only lightly tested?
    - VideoCentroidProcessor and LargestGroupFinder
    - For VideoCentroidProcessor there is no consistent way to test, so we oupted checking structure and output.
    
    Where would be the highest priority places to add new tests?
    - VideoCentroidProcessor for more coverage
    
    Other testing improvements?
    - TBA
