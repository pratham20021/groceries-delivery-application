#!/bin/bash
# Run this ONCE manually before first Jenkins pipeline run.
# Creates the S3 bucket and DynamoDB table for Terraform remote state.

set -e

REGION="us-east-1"
BUCKET="revcart-terraform-state"
TABLE="revcart-terraform-locks"

echo "Creating S3 bucket: $BUCKET"
aws s3api create-bucket \
    --bucket $BUCKET \
    --region $REGION

aws s3api put-bucket-versioning \
    --bucket $BUCKET \
    --versioning-configuration Status=Enabled

aws s3api put-bucket-encryption \
    --bucket $BUCKET \
    --server-side-encryption-configuration '{
        "Rules": [{
            "ApplyServerSideEncryptionByDefault": {
                "SSEAlgorithm": "AES256"
            }
        }]
    }'

aws s3api put-public-access-block \
    --bucket $BUCKET \
    --public-access-block-configuration \
        "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"

echo "Creating DynamoDB table: $TABLE"
aws dynamodb create-table \
    --table-name $TABLE \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region $REGION

echo "Done. S3 bucket and DynamoDB lock table are ready."
