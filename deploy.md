# Deployment

## Build docker image
* `docker login`
* `mvn compile jib:build`

## Deploy to Azure
* `az account list`
* `terraform plan`
* `terraform refresh`
* `terraform apply`
* `terraform destroy`