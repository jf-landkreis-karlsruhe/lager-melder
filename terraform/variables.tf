variable "prefix" {
  description = "The prefix used for all resources in this example"
  default     = "lager-melder"
}

variable "location" {
  description = "The Azure location where all resources in this example should be created"
  default = "northeurope"
}

variable "subscription_id" {
  description = "Azure Subscription ID to be used for billing"
}

variable "my_sql_master_password" {
  description = "MySql master password"
}

variable "docker_image" {
  description = "Docker image name"
  default = "kordondev/lager-melder"
}

variable "docker_image_tag" {
  description = "Docker image tag"
  default = "latest"
}