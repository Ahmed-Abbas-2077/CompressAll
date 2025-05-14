import os
import argparse
from PIL import Image
import glob


def resize_image(input_path, output_path, scale_factor):
    """
    Resize an image by the given scale factor
    """
    try:
        with Image.open(input_path) as img:
            # Get original dimensions
            original_width, original_height = img.size

            # Calculate new dimensions
            new_width = int(original_width / scale_factor)
            new_height = int(original_height / scale_factor)

            # Resize image
            resized_img = img.resize((new_width, new_height), Image.LANCZOS)

            # Create directory if it doesn't exist
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            # Save resized image
            resized_img.save(output_path)
            print(
                f"Resized {input_path} from {original_width}x{original_height} to {new_width}x{new_height}")

            return True
    except Exception as e:
        print(f"Error processing {input_path}: {e}")
        return False


def process_directory(input_dir, output_dir, scale_factor, recursive=False):
    """
    Process all images in a directory
    """
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # Get all image files in directory
    image_extensions = ['*.jpg', '*.jpeg', '*.png', '*.bmp', '*.gif']
    image_files = []

    for ext in image_extensions:
        if recursive:
            image_files.extend(glob.glob(os.path.join(
                input_dir, '**', ext), recursive=True))
        else:
            image_files.extend(glob.glob(os.path.join(input_dir, ext)))

    # Process each image
    success_count = 0
    for input_path in image_files:
        # Create equivalent path in output directory
        rel_path = os.path.relpath(input_path, input_dir)
        output_path = os.path.join(output_dir, rel_path)

        if resize_image(input_path, output_path, scale_factor):
            success_count += 1

    print(
        f"Successfully processed {success_count} of {len(image_files)} images")


def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(
        description='Resize images by a scale factor')
    parser.add_argument('input', help='Input image file or directory')
    parser.add_argument('output', help='Output image file or directory')
    parser.add_argument('--scale', type=float, default=2.0,
                        help='Scale factor to reduce by (e.g., 2.0 means half size)')
    parser.add_argument('--recursive', action='store_true',
                        help='Process directories recursively')

    args = parser.parse_args()

    # Check if input is a file or directory
    if os.path.isfile(args.input):
        resize_image(args.input, args.output, args.scale)
    elif os.path.isdir(args.input):
        process_directory(args.input, args.output, args.scale, args.recursive)
    else:
        print(f"Error: {args.input} is not a valid file or directory")


if __name__ == "__main__":
    main()
