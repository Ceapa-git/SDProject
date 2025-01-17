#!/bin/bash

# Define the pattern to search for
SEARCH="spring.jpa.hibernate.ddl-auto=validate"
REPLACE="spring.jpa.hibernate.ddl-auto=create"

# Function to find and replace in all application.properties files
replace_property() {
    local search="$1"
    local replace="$2"
    echo "Updating properties files: $search -> $replace"
    find . -type f -name "application.properties" -exec sed -i "s|^$search|$replace|g" {} +
}

# Step 1: Replace "validate" with "create"
replace_property "$SEARCH" "$REPLACE"

# Step 2: Run docker-build
echo "Running docker-build..."
docker-compose up --build -d

# Step 3: Replace "create" back to "validate"
replace_property "$REPLACE" "$SEARCH"

echo "All tasks completed."
